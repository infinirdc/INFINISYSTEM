package com.infinisystem.data

import android.app.usage.UsageStatsManager
import android.content.Context
import kotlinx.coroutines.flow.Flow

class UsageStatsRepository(private val usageStatDao: UsageStatDao, private val context: Context) {

    private val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    fun getUsageStats(since: Long): Flow<List<UsageStatEntity>> {
        return usageStatDao.getUsageStats(since)
    }

    suspend fun refreshUsageStats() {
        val time = System.currentTimeMillis()
        // Query stats for the last 24 hours
        val stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 60 * 60 * 24, time)
        stats?.forEach { 
            if (it.totalTimeInForeground > 0) {
                val usageStat = UsageStatEntity(
                    packageName = it.packageName,
                    timestamp = it.lastTimeUsed,
                    totalTimeInForeground = it.totalTimeInForeground
                )
                usageStatDao.insertUsageStat(usageStat)
            }
        }
    }
}
