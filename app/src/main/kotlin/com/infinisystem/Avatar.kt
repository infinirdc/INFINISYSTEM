package com.infinisystem

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun Avatar(modifier: Modifier = Modifier) {
    val batteryLevel = batteryLevelAsFlow(LocalContext.current).collectAsState(initial = 0f)

    val infiniteTransition = rememberInfiniteTransition(label = "avatar-pulse")
    val pulse = infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val rotation = infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Canvas(modifier = modifier.size(200.dp)) {
        val scale = pulse.value * (batteryLevel.value / 100f)
        val angle = rotation.value * (Math.PI / 180f).toFloat()

        val points = mutableListOf<Offset>()
        val vertices = 8 // Octaèdre
        for (i in 0 until vertices) {
            val x = center.x + (size.minDimension / 2) * scale * cos(i * 2 * Math.PI / vertices + angle).toFloat()
            val y = center.y + (size.minDimension / 2) * scale * sin(i * 2 * Math.PI / vertices + angle).toFloat()
            points.add(Offset(x, y))
        }
        
        // Dessiner les arêtes de l'octaèdre
        val edges = listOf(
            0 to 1, 1 to 2, 2 to 3, 3 to 0,
            4 to 5, 5 to 6, 6 to 7, 7 to 4,
            0 to 4, 1 to 5, 2 to 6, 3 to 7
        )

        edges.forEach { (start, end) ->
             if (start < points.size && end < points.size) {
                drawLine(
                    color = Color.Cyan,
                    start = points[start],
                    end = points[end],
                    strokeWidth = 2f
                )
            }
        }
    }
}

private fun batteryLevelAsFlow(context: Context): Flow<Float> = flow {
    val intentFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
    val batteryStatus: Intent? = context.registerReceiver(null, intentFilter)
    val level: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
    val scale: Int = batteryStatus?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
    emit(level * 100 / scale.toFloat())
}
