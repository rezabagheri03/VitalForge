package com.vitalforge.watch.data.health

import android.content.Context
import android.util.Log
import androidx.health.services.client.ExerciseClient
import androidx.health.services.client.HealthServices
import androidx.health.services.client.HealthServicesClient
import androidx.health.services.client.MeasureClient
import androidx.health.services.client.PassiveMonitoringClient
import androidx.health.services.client.data.*
import androidx.health.services.client.data.DataType.Companion.HEART_RATE_BPM
import androidx.health.services.client.data.DataType.Companion.HEART_RATE_BPM_STATS
import androidx.health.services.client.data.DataType.Companion.CALORIES_TOTAL
import androidx.health.services.client.data.DataType.Companion.DISTANCE_TOTAL
import androidx.health.services.client.data.DataType.Companion.SPO2_PERCENTAGE
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthServicesManager @Inject constructor(
    context: Context
) {
    private val client: HealthServicesClient = HealthServices.getClient(context)
    private val measureClient: MeasureClient = client.measureClient
    private val passiveClient: PassiveMonitoringClient = client.passiveMonitoringClient
    private val exerciseClient: ExerciseClient = client.exerciseClient

    private val _dataFlow = MutableSharedFlow<HealthServicesData>()
    val dataFlow: Flow<HealthServicesData> = _dataFlow

    companion object { private const val TAG = "HealthServicesMgr" }

    suspend fun initialize(): Boolean {
        try {
            val caps = measureClient.getCapabilitiesAsync().await()
            Log.d(TAG, "Measure caps: $caps")
            passiveClient.getCapabilitiesAsync().await().let { Log.d(TAG, "Passive caps: $it") }
            exerciseClient.getCapabilitiesAsync().await().let { Log.d(TAG, "Exercise caps: $it") }
            return true
        } catch (e: Exception) {
            Log.e(TAG, "Init error", e)
            return false
        }
    }

    suspend fun startPassiveMonitoring(): Boolean {
        return try {
            val config = PassiveMonitoringConfig(
                setOf(HEART_RATE_BPM, SPO2_PERCENTAGE),
                shouldUserActivityInfoBeRequested = true
            )
            passiveClient.setPassiveListenerServiceAsync(
                HealthServicesPassiveDataService::class.java, config
            ).await()
            Log.d(TAG, "Passive monitoring started")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Passive start error", e)
            false
        }
    }

    suspend fun startHeartRateMeasurement(): Boolean {
        val callback = object : MeasureCallback {
            override fun onAvailabilityChanged(dataType: DataType<*, *>, availability: Availability) {
                Log.d(TAG, "HR avail: $availability")
            }
            override fun onDataReceived(container: DataPointContainer) {
                container.getData(HEART_RATE_BPM).lastOrNull()?.let { dp ->
                    _dataFlow.tryEmit(
                        HealthServicesData(
                            heartRate = dp.value.toInt(),
                            timestamp = dp.timeDurationFromBoot.toEpochMilli(),
                            dataSource = DataSource.HEALTH_SERVICES,
                            accuracy = DataAccuracy.HIGH
                        )
                    )
                }
            }
        }
        return try {
            measureClient.registerMeasureCallback(HEART_RATE_BPM, callback)
            Log.d(TAG, "HR measure started")
            true
        } catch (e: Exception) {
            Log.e(TAG, "HR measure error", e)
            false
        }
    }

    suspend fun startSpO2Measurement(): Boolean {
        return try {
            val caps = measureClient.getCapabilitiesAsync().await()
            if (SPO2_PERCENTAGE in caps.supportedDataTypesMeasure) {
                val callback = object : MeasureCallback {
                    override fun onAvailabilityChanged(dt: DataType<*, *>, av: Availability) {
                        Log.d(TAG, "SpO2 avail: $av")
                    }
                    override fun onDataReceived(container: DataPointContainer) {
                        container.getData(SPO2_PERCENTAGE).lastOrNull()?.let { dp ->
                            _dataFlow.tryEmit(
                                HealthServicesData(
                                    spO2 = dp.value,
                                    timestamp = dp.timeDurationFromBoot.toEpochMilli(),
                                    dataSource = DataSource.HEALTH_SERVICES,
                                    accuracy = DataAccuracy.HIGH
                                )
                            )
                        }
                    }
                }
                measureClient.registerMeasureCallback(SPO2_PERCENTAGE, callback)
                Log.d(TAG, "SpO2 measure started")
                true
            } else {
                Log.w(TAG, "SpO2 not supported")
                false
            }
        } catch (e: Exception) {
            Log.e(TAG, "SpO2 start error", e)
            false
        }
    }

    suspend fun startExerciseSession(type: ExerciseType): Boolean {
        return try {
            val config = ExerciseConfig(
                exerciseType = type,
                dataTypes = setOf(HEART_RATE_BPM, CALORIES_TOTAL, DISTANCE_TOTAL),
                isAutoPauseAndResumeEnabled = true,
                isGpsEnabled = true
            )
            exerciseClient.startExerciseAsync(config).await()
            val cb = object : ExerciseUpdateCallback {
                override fun onExerciseUpdateReceived(update: ExerciseUpdate) {
                    update.latestMetrics.getData(HEART_RATE_BPM).lastOrNull()?.let { dp ->
                        _dataFlow.tryEmit(
                            HealthServicesData(
                                heartRate = dp.value.toInt(),
                                timestamp = System.currentTimeMillis(),
                                dataSource = DataSource.HEALTH_SERVICES,
                                accuracy = DataAccuracy.HIGH,
                                exerciseData = ExerciseSessionData(
                                    exerciseState = update.exerciseStateInfo.state,
                                    heartRate = dp.value.toInt(),
                                    calories = update.latestMetrics.getData(CALORIES_TOTAL).lastOrNull()?.value,
                                    distance = update.latestMetrics.getData(DISTANCE_TOTAL).lastOrNull()?.value,
                                    timestamp = System.currentTimeMillis()
                                )
                            )
                        )
                    }
                }
                override fun onLapSummaryReceived(lap: ExerciseLapSummary) {}
                override fun onRegistered() {}
                override fun onRegistrationFailed(t: Throwable) { Log.e(TAG,"Ex reg fail",t) }
                override fun onAvailabilityChanged(dt: DataType<*, *>, av: Availability) {}
            }
            exerciseClient.setUpdateCallback(cb)
            Log.d(TAG, "Exercise started")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Exercise error", e)
            false
        }
    }

    suspend fun stopAllMeasurements() {
        measureClient.unregisterMeasureCallbackAsync(HEART_RATE_BPM).await()
        measureClient.unregisterMeasureCallbackAsync(SPO2_PERCENTAGE).await()
        passiveClient.clearPassiveListenerServiceAsync().await()
        Log.d(TAG, "All measurements stopped")
    }
}
