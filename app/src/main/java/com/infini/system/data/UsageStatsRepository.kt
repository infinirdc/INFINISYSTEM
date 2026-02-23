package com.infini.system.data

import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Calendar

class UsageStatsRepository(private val context: Context) {
    
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun getDailyUsageStats(): Flow<List<AppUsageInfo>> = flow {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()
        
        val usageStatsList = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY,
            startTime,
            endTime
        )
        
        val appUsageList = usageStatsList.map { usageStats ->
            AppUsageInfo(
                packageName = usageStats.packageName,
                totalTimeInForeground = usageStats.totalTimeInForeground,
                lastTimeUsed = usageStats.lastTimeUsed
            )
        }.sortedByDescending { it.totalTimeInForeground }
        
        emit(appUsageList)
    }
    
    data class AppUsageInfo(
        val packageName: String,
        val totalTimeInForeground: Long,
        val lastTimeUsed: Long
    )
}