package com.infinisystem.data

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class UsageStatsWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = UsageStatsRepository(database.usageStatDao(), applicationContext)

        return try {
            repository.refreshUsageStats()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
