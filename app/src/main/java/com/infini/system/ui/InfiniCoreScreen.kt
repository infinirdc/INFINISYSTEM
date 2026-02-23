package com.infini.system.ui

import android.content.Context
import android.content.Intent
import android.os.BatteryManager
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import com.infini.system.ui.theme.DeepBlack
import com.infini.system.ui.theme.NeonCyan
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun InfiniCoreScreen() {
    val context = LocalContext.current
    var batteryLevel by remember { mutableStateOf(getBatteryLevel(context)) }
    var animationTime by remember { mutableStateOf(0f) }
    
    // Update battery level periodically
    LaunchedEffect(Unit) {
        while (true) {
            delay(60000) // Update every minute
            batteryLevel = getBatteryLevel(context)
        }
    }
    
    // Animation loop for the avatar
    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos { frameTime ->
                animationTime = frameTime / 1_000_000_000f
            }
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlack)
    ) {
        // Render the animated avatar
        AnimatedAvatar(
            batteryLevel = batteryLevel,
            animationTime = animationTime
        )
    }
}

@Composable
fun AnimatedAvatar(batteryLevel: Float, animationTime: Float) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val maxRadius = size.minDimension / 4
        
        // Calculate pulsing effect based on battery level and time
        val pulseFactor = 0.8f + 0.2f * sin(animationTime * 2) * batteryLevel
        val radius = maxRadius * pulseFactor
        
        // Draw octahedron-like structure with neon effect
        drawOctahedron(
            center = Offset(centerX, centerY),
            radius = radius,
            rotation = animationTime,
            color = NeonCyan
        )
        
        // Draw subtle grid lines for cyberpunk effect
        drawCyberGrid(size, animationTime)
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawOctahedron(
    center: Offset,
    radius: Float,
    rotation: Float,
    color: Color
) {
    val path = Path()
    
    // Create points for an octahedron (simplified 2D representation)
    val points = mutableListOf<Offset>()
    for (i in 0 until 8) {
        val angle = rotation + (2 * Math.PI * i / 8).toFloat()
        val x = center.x + radius * cos(angle)
        val y = center.y + radius * sin(angle)
        points.add(Offset(x, y))
    }
    
    // Connect points to form octahedron edges
    for (i in points.indices) {
        for (j in i + 1 until points.size) {
            // Only connect certain points to form the shape
            if ((i - j).absoluteValue == 1 || 
                (i == 0 && j == points.size - 1) ||
                (i == 2 && j == 6) ||
                (i == 1 && j == 5)) {
                
                drawLine(
                    color = color,
                    start = points[i],
                    end = points[j],
                    strokeWidth = 3f,
                    alpha = 0.7f
                )
            }
        }
    }
    
    // Draw central pulsing circle
    drawCircle(
        color = color,
        radius = radius * 0.3f,
        center = center,
        alpha = 0.5f + 0.2f * sin(rotation * 3)
    )
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawCyberGrid(
    size: androidx.compose.ui.geometry.Size,
    time: Float
) {
    val gridSize = 50f
    val offset = (time * 20) % gridSize
    
    for (x in 0..(size.width / gridSize).toInt()) {
        drawLine(
            color = Color.White,
            start = Offset(x * gridSize + offset, 0f),
            end = Offset(x * gridSize + offset, size.height),
            strokeWidth = 1f,
            alpha = 0.05f
        )
    }
    
    for (y in 0..(size.height / gridSize).toInt()) {
        drawLine(
            color = Color.White,
            start = Offset(0f, y * gridSize - offset),
            end = Offset(size.width, y * gridSize - offset),
            strokeWidth = 1f,
            alpha = 0.05f
        )
    }
}

private fun getBatteryLevel(context: Context): Float {
    return try {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) / 100f
        } else {
            val intent = context.registerReceiver(null, Intent.ACTION_BATTERY_CHANGED)
            val level = intent?.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) ?: -1
            val scale = intent?.getIntExtra(BatteryManager.EXTRA_SCALE, -1) ?: -1
            level / scale.toFloat()
        }
    } catch (e: Exception) {
        1.0f // Default to full if unable to determine
    }
}