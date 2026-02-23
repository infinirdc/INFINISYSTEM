package com.infinisystem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel: InfiniCoreViewModel = viewModel(
                factory = InfiniCoreViewModelFactory(application)
            )
            InfiniCoreScreen(viewModel)
        }
    }
}

@Composable
fun InfiniCoreScreen(viewModel: InfiniCoreViewModel) {
    val mostUsedApps by viewModel.mostUsedApps.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF050505)),
        contentAlignment = Alignment.Center
    ) {
        Avatar()
        AppList(apps = mostUsedApps, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun AppList(apps: List<AppUsageInfo>, modifier: Modifier = Modifier) {
    LazyColumn(modifier = modifier.height(300.dp).padding(bottom = 32.dp)) {
        items(apps) { appInfo ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = appInfo.appInfo.loadIcon(LocalContext.current.packageManager),
                    contentDescription = "App Icon",
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = appInfo.appInfo.loadLabel(LocalContext.current.packageManager).toString(),
                    color = Color.Cyan,
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    // Preview will not show real data, but we can display the layout.
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF050505)))
}
