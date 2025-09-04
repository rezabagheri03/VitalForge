package com.vitalforge.watch

import android.app.Application
import android.util.Log
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.vitalforge.watch.data.work.HealthDataCleanupWorker
import com.vitalforge.watch.data.work.HealthDataProcessingWorker
import com.vitalforge.watch.util.NotificationChannelManager
import dagger.hilt.android.HiltAndroidApp
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class VitalForgeApplication : Application(), Configuration.Provider {
    companion object {
        private const val TAG = "VitalForgeApp"
        private const val WORK_PROCESSING = "health_data_processing"
        private const val WORK_CLEANUP = "health_data_cleanup"
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "App initializing")
        NotificationChannelManager.createNotificationChannels(this)
        scheduleBackgroundWork()
    }

    private fun scheduleBackgroundWork() {
        val wm = WorkManager.getInstance(this)
        val procConstraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()
        val procWork = PeriodicWorkRequestBuilder<HealthDataProcessingWorker>(
            15, TimeUnit.MINUTES, 5, TimeUnit.MINUTES
        )
            .setConstraints(procConstraints)
            .build()
        wm.enqueueUniquePeriodicWork(WORK_PROCESSING, ExistingPeriodicWorkPolicy.KEEP, procWork)

        val cleanConstraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiresCharging(true)
            .build()
        val cleanWork = PeriodicWorkRequestBuilder<HealthDataCleanupWorker>(
            1, TimeUnit.DAYS
        )
            .setConstraints(cleanConstraints)
            .build()
        wm.enqueueUniquePeriodicWork(WORK_CLEANUP, ExistingPeriodicWorkPolicy.KEEP, cleanWork)

        Log.d(TAG, "Background work scheduled")
    }

    override fun getWorkManagerConfiguration() =
        Configuration.Builder()
            .setMinimumLoggingLevel(if (BuildConfig.DEBUG) Log.DEBUG else Log.INFO)
            .build()
}
