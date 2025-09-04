package com.vitalforge.watch.data.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.vitalforge.watch.data.repository.VitalForgeRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

class HealthDataCleanupWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val repository: VitalForgeRepository
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        return try {
            repository.cleanupOldData("default_user")
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
