package com.infinisystem.data

import android.app.usage.UsageStatsManager
import android.content.Context
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UsageStatsRepository(private val context: Context) {

    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    fun getUsageStats(): Flow<Map<String, Long>> = flow {
        val time = System.currentTimeMillis()
        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 60 * 60 * 24, time)
        val usageMap = mutableMapOf<String, Long>()
        stats?.forEach { 
            if (it.totalTimeInForeground > 0) {
                usageMap[it.packageName] = it.totalTimeInForeground
            }
        }
        emit(usageMap)
    }
}
