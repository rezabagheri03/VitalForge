package com.vitalforge.watch.util

import android.content.Context
import androidx.work.*
import com.vitalforge.watch.data.work.HealthDataCleanupWorker
import com.vitalforge.watch.data.work.HealthDataProcessingWorker
import java.util.concurrent.TimeUnit

object WorkManagerHelper {
    private const val WORK_PROCESSING = "health_data_processing"
    private const val WORK_CLEANUP = "health_data_cleanup"

    fun scheduleAll(context: Context) {
        val wm = WorkManager.getInstance(context)

        val procConstraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
        val procReq = PeriodicWorkRequestBuilder<HealthDataProcessingWorker>(
            15, TimeUnit.MINUTES, 5, TimeUnit.MINUTES
        )
            .setConstraints(procConstraints)
            .build()
        wm.enqueueUniquePeriodicWork(
            WORK_PROCESSING, ExistingPeriodicWorkPolicy.KEEP, procReq
        )

        val cleanConstraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .build()
        val cleanReq = PeriodicWorkRequestBuilder<HealthDataCleanupWorker>(
            1, TimeUnit.DAYS
        )
            .setConstraints(cleanConstraints)
            .build()
        wm.enqueueUniquePeriodicWork(
            WORK_CLEANUP, ExistingPeriodicWorkPolicy.KEEP, cleanReq
        )
    }
}
