package com.infinisystem

import android.app.Application
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.infinisystem.data.UsageStatEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class AppUsageInfo(val appInfo: ApplicationInfo, val totalTimeInForeground: Long)

class InfiniCoreViewModel(application: Application) : AndroidViewModel(application) {

    private val usageStatsRepository = (application as InfiniSystemApp).repository
    private val packageManager = application.packageManager

    val mostUsedApps: StateFlow<List<AppUsageInfo>> = usageStatsRepository.getUsageStats(System.currentTimeMillis() - 1000 * 60 * 60 * 24 * 7) // Last 7 days
        .map { stats ->
            stats.groupBy { it.packageName }
                .map {
                    val totalTime = it.value.sumOf { stat -> stat.totalTimeInForeground }
                    val appInfo = try {
                        packageManager.getApplicationInfo(it.key, 0)
                    } catch (e: PackageManager.NameNotFoundException) {
                        null
                    }
                    appInfo?.let { info -> AppUsageInfo(info, totalTime) }
                }
                .filterNotNull()
                .sortedByDescending { it.totalTimeInForeground }
                .take(10)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}

class InfiniCoreViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InfiniCoreViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InfiniCoreViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
