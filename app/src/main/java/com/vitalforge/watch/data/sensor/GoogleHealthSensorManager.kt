package com.vitalforge.watch.data.sensor

import android.content.Context
import android.util.Log
import androidx.health.services.client.HealthServicesClient
import androidx.health.services.client.data.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Google Health Services implementation as alternative to Samsung Health SDK
 */
@Singleton
class GoogleHealthSensorManager @Inject constructor(
    private val context: Context
) {
    private val healthServicesClient = HealthServicesClient.getOrCreate(context)
    private val activeMeasureClient = healthServicesClient.measureClient
    private val activePassiveMonitoringClient = healthServicesClient.passiveMonitoringClient

    private val _heartRateFlow = MutableSharedFlow<HeartRateData>()
    val heartRateFlow: Flow<HeartRateData> = _heartRateFlow

    private val _spO2Flow = MutableSharedFlow<SpO2Data>()
    val spO2Flow: Flow<SpO2Data> = _spO2Flow

    companion object {
        private const val TAG = "GoogleHealthSensor"
    }

    /**
     * Start heart rate monitoring using Google Health Services
     */
    suspend fun startHeartRateMonitoring(): Boolean = suspendCancellableCoroutine { cont ->
        try {
            val capability = activeMeasureClient.getCapabilities()
            if (capability.supportedDataTypes.contains(DataType.HEART_RATE_BPM)) {
                val measureCallback = object : MeasureCallback {
                    override fun onAvailabilityChanged(dataType: DataType, availability: Availability) {
                        Log.d(TAG, "Heart rate availability: $availability")
                    }

                    override fun onDataReceived(data: DataPointContainer) {
                        data.getData(DataType.HEART_RATE_BPM).forEach { dataPoint ->
                            val heartRate = dataPoint.value as Double
                            val data = HeartRateData(
                                heartRate = heartRate.toInt(),
                                status = HeartRateStatus.SUCCESS,
                                timestamp = dataPoint.time.toEpochMilli(),
                                ibiValues = emptyList(),
                                confidence = 1.0f
                            )
                            _heartRateFlow.tryEmit(data)
                        }
                    }
                }

                activeMeasureClient.startMeasure(
                    MeasureRequest.builder(DataType.HEART_RATE_BPM)
                        .setCallback(measureCallback)
                        .build()
                )
                cont.resume(true)
            } else {
                Log.w(TAG, "Heart rate monitoring not supported")
                cont.resume(false)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start heart rate monitoring", e)
            cont.resumeWithException(e)
        }
    }

    /**
     * Start passive monitoring for continuous data collection
     */
    suspend fun startPassiveMonitoring(): Boolean = suspendCancellableCoroutine { cont ->
        try {
            val passiveListener = object : PassiveListener {
                override fun onNewDataPointsReceived(dataPoints: DataPointContainer) {
                    // Process received data points
                    dataPoints.getData(DataType.HEART_RATE_BPM).forEach { dataPoint ->
                        val heartRate = dataPoint.value as Double
                        val data = HeartRateData(
                            heartRate = heartRate.toInt(),
                            status = HeartRateStatus.SUCCESS,
                            timestamp = dataPoint.time.toEpochMilli(),
                            ibiValues = emptyList(),
                            confidence = 1.0f
                        )
                        _heartRateFlow.tryEmit(data)
                    }
                }
            }

            val passiveMonitoringConfig = PassiveMonitoringConfig.builder()
                .setDataTypes(setOf(DataType.HEART_RATE_BPM))
                .build()

            activePassiveMonitoringClient.setPassiveMonitoringCallback(passiveListener)
            activePassiveMonitoringClient.startPassiveMonitoring(passiveMonitoringConfig)
            
            cont.resume(true)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start passive monitoring", e)
            cont.resumeWithException(e)
        }
    }

    /**
     * Stop all monitoring
     */
    suspend fun stopMonitoring() {
        try {
            activeMeasureClient.stopMeasure(DataType.HEART_RATE_BPM)
            activePassiveMonitoringClient.clearPassiveMonitoringCallback()
            Log.d(TAG, "Monitoring stopped")
        } catch (e: Exception) {
            Log.e(TAG, "Error stopping monitoring", e)
        }
    }
}

// Data classes (same as Samsung implementation)
data class HeartRateData(
    val heartRate: Int,
    val status: HeartRateStatus,
    val timestamp: Long,
    val ibiValues: List<Int>,
    val confidence: Float
)

data class SpO2Data(
    val oxygenSaturation: Float,
    val status: SpO2Status,
    val timestamp: Long,
    val confidence: Float
)

enum class HeartRateStatus { SUCCESS, NO_DATA_FLUSH, DEVICE_MOVING, UNRELIABLE, UNKNOWN }
enum class SpO2Status { SUCCESSFUL_MEASUREMENT, MEASUREMENT_FAILED, DEVICE_MOVING, FINGER_NOT_DETECTED, UNKNOWN }