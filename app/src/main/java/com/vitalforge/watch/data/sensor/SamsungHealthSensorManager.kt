package com.vitalforge.watch.data.sensor

import android.content.Context
import android.util.Log
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.ValueKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

@Singleton
class SamsungHealthSensorManager @Inject constructor(
    private val context: Context
) {
    private var healthTrackingService: HealthTrackingService? = null
    private val activeTrackers = mutableMapOf<HealthTrackerType, HealthTracker>()

    private val _heartRateFlow = MutableSharedFlow<HeartRateData>()
    val heartRateFlow: Flow<HeartRateData> = _heartRateFlow

    private val _spO2Flow = MutableSharedFlow<SpO2Data>()
    val spO2Flow: Flow<SpO2Data> = _spO2Flow

    private val _ppgFlow = MutableSharedFlow<PpgData>()
    val ppgFlow: Flow<PpgData> = _ppgFlow

    private val _ecgFlow = MutableSharedFlow<EcgData>()
    val ecgFlow: Flow<EcgData> = _ecgFlow

    companion object {
        private const val TAG = "SamsungHealthSensor"
    }

    /**
     * Initialize Samsung Health SDK connection
     */
    suspend fun initialize(): Boolean = suspendCancellableCoroutine { cont ->
        try {
            healthTrackingService = HealthTrackingService(object : HealthTrackingService.ConnectionListener {
                override fun onConnectionSuccess() {
                    Log.d(TAG, "Connected to Samsung Health SDK")
                    cont.resume(true)
                }

                override fun onConnectionFailed(exception: HealthTrackerException) {
                    Log.e(TAG, "Connection failed", exception)
                    cont.resumeWithException(exception)
                }

                override fun onConnectionEnded() {
                    Log.d(TAG, "Connection ended")
                }
            }, context)
            healthTrackingService?.connectService()
        } catch (e: Exception) {
            Log.e(TAG, "Initialization error", e)
            cont.resumeWithException(e)
        }
    }

    /**
     * Start continuous heart rate monitoring
     */
    suspend fun startHeartRateMonitoring(): Boolean {
        return startTracking(HealthTrackerType.HEART_RATE_CONTINUOUS, object : HealthTracker.TrackerEventListener {
            override fun onDataReceived(dataPoints: List<DataPoint>) {
                dataPoints.forEach { dp ->
                    try {
                        val hr = dp.getValue(ValueKey.HeartRateSet.HEART_RATE)
                        val status = dp.getValue(ValueKey.HeartRateSet.HEART_RATE_STATUS)
                        val ibiList = dp.getValue(ValueKey.HeartRateSet.IBI_LIST)
                        val ibiStatus = dp.getValue(ValueKey.HeartRateSet.IBI_STATUS_LIST)
                        val data = HeartRateData(
                            heartRate = hr,
                            status = mapHeartRateStatus(status),
                            timestamp = dp.timestamp,
                            ibiValues = processIbiData(ibiList, ibiStatus),
                            confidence = calculateHRConfidence(status)
                        )
                        _heartRateFlow.tryEmit(data)
                    } catch (e: Exception) {
                        Log.e(TAG, "Processing HR data error", e)
                    }
                }
            }

            override fun onError(error: HealthTracker.TrackerError) {
                Log.e(TAG, "HR tracking error: $error")
            }

            override fun onFlushCompleted() {
                Log.d(TAG, "HR data flush completed")
            }
        })
    }

    /**
     * Start SpO2 monitoring
     */
    suspend fun startSpO2Monitoring(): Boolean {
        return startTracking(HealthTrackerType.SPO2_ON_DEMAND, object : HealthTracker.TrackerEventListener {
            override fun onDataReceived(dataPoints: List<DataPoint>) {
                dataPoints.forEach { dp ->
                    try {
                        val sp = dp.getValue(ValueKey.SpO2Set.SPO2)
                        val status = dp.getValue(ValueKey.SpO2Set.STATUS)
                        val data = SpO2Data(
                            oxygenSaturation = sp.toFloat(),
                            status = mapSpO2Status(status),
                            timestamp = dp.timestamp,
                            confidence = calculateSpO2Confidence(status)
                        )
                        _spO2Flow.tryEmit(data)
                    } catch (e: Exception) {
                        Log.e(TAG, "Processing SpO2 error", e)
                    }
                }
            }

            override fun onError(error: HealthTracker.TrackerError) {
                Log.e(TAG, "SpO2 tracking error: $error")
            }

            override fun onFlushCompleted() {}
        })
    }

    /**
     * Start PPG monitoring
     */
    suspend fun startPpgMonitoring(): Boolean {
        return startTracking(HealthTrackerType.PPG_GREEN, object : HealthTracker.TrackerEventListener {
            override fun onDataReceived(dataPoints: List<DataPoint>) {
                dataPoints.forEach { dp ->
                    try {
                        val green = dp.getValue(ValueKey.PpgGreenSet.PPG_GREEN)
                        val ir = dp.getValue(ValueKey.PpgGreenSet.PPG_IR) ?: 0
                        val red = dp.getValue(ValueKey.PpgGreenSet.PPG_RED) ?: 0
                        val data = PpgData(
                            greenChannel = green,
                            irChannel = ir,
                            redChannel = red,
                            timestamp = dp.timestamp,
                            samplingRate = 25
                        )
                        _ppgFlow.tryEmit(data)
                    } catch (e: Exception) {
                        Log.e(TAG, "PPG processing error", e)
                    }
                }
            }

            override fun onError(error: HealthTracker.TrackerError) {
                Log.e(TAG, "PPG tracking error: $error")
            }

            override fun onFlushCompleted() {}
        })
    }

    /**
     * Start ECG monitoring
     */
    suspend fun startEcgMonitoring(): Boolean {
        return startTracking(HealthTrackerType.ECG, object : HealthTracker.TrackerEventListener {
            override fun onDataReceived(dataPoints: List<DataPoint>) {
                dataPoints.forEach { dp ->
                    try {
                        val signal = dp.getValue(ValueKey.EcgSet.ECG_RAW_DATA)
                        val leadOff = dp.getValue(ValueKey.EcgSet.LEAD_OFF_DETECTED)
                        val maxReached = dp.getValue(ValueKey.EcgSet.MAX_THRESHOLD_REACHED)
                        val data = EcgData(
                            rawSignal = signal,
                            samplingRate = 500,
                            timestamp = dp.timestamp,
                            leadOffDetected = leadOff,
                            maxThresholdReached = maxReached,
                            quality = calculateEcgQuality(signal, leadOff)
                        )
                        _ecgFlow.tryEmit(data)
                    } catch (e: Exception) {
                        Log.e(TAG, "ECG processing error", e)
                    }
                }
            }

            override fun onError(error: HealthTracker.TrackerError) {
                Log.e(TAG, "ECG tracking error: $error")
            }

            override fun onFlushCompleted() {}
        })
    }

    /**
     * Generic startTracking
     */
    private suspend fun startTracking(
        type: HealthTrackerType,
        listener: HealthTracker.TrackerEventListener
    ): Boolean = suspendCancellableCoroutine { cont ->
        try {
            val tracker = healthTrackingService?.getHealthTracker(type)
            if (tracker == null) {
                cont.resume(false)
                return@suspendCancellableCoroutine
            }
            activeTrackers[type] = tracker
            tracker.setEventListener(listener)
            Log.d(TAG, "Started $type")
            cont.resume(true)
        } catch (e: Exception) {
            Log.e(TAG, "Tracking error for $type", e)
            cont.resumeWithException(e)
        }
    }

    /**
     * Stop all tracking
     */
    fun stopAllTracking() {
        activeTrackers.keys.forEach { type ->
            activeTrackers[type]?.unsetEventListener()
        }
        activeTrackers.clear()
        healthTrackingService?.disconnectService()
    }

    // Helper methods

    private fun mapHeartRateStatus(status: Int) = when (status) {
        0 -> HeartRateStatus.SUCCESS
        1 -> HeartRateStatus.NO_DATA_FLUSH
        2 -> HeartRateStatus.DEVICE_MOVING
        3 -> HeartRateStatus.UNRELIABLE
        else -> HeartRateStatus.UNKNOWN
    }

    private fun mapSpO2Status(status: Int) = when (status) {
        0 -> SpO2Status.SUCCESSFUL_MEASUREMENT
        1 -> SpO2Status.MEASUREMENT_FAILED
        2 -> SpO2Status.DEVICE_MOVING
        3 -> SpO2Status.FINGER_NOT_DETECTED
        else -> SpO2Status.UNKNOWN
    }

    private fun processIbiData(ibi: IntArray?, status: IntArray?): List<Int> {
        if (ibi == null || status == null) return emptyList()
        return ibi.filterIndexed { i, v -> status.getOrNull(i) == 0 && v > 0 }
    }

    private fun calculateHRConfidence(status: Int) = when (status) {
        0 -> 1.0f
        1 -> 0.7f
        2 -> 0.3f
        else -> 0.1f
    }

    private fun calculateSpO2Confidence(status: Int) = if (status == 0) 1.0f else 0.0f

    private fun calculateEcgQuality(signal: IntArray, leadOff: Boolean): EcgQuality {
        if (leadOff) return EcgQuality.POOR
        val mean = signal.average()
        val variance = signal.map { (it - mean) * (it - mean) }.average()
        return when {
            variance > 1000 -> EcgQuality.GOOD
            variance > 500 -> EcgQuality.FAIR
            else -> EcgQuality.POOR
        }
    }
}

// Data classes and enums referenced above
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

data class PpgData(
    val greenChannel: Int,
    val irChannel: Int,
    val redChannel: Int,
    val timestamp: Long,
    val samplingRate: Int
)

data class EcgData(
    val rawSignal: IntArray,
    val samplingRate: Int,
    val timestamp: Long,
    val leadOffDetected: Boolean,
    val maxThresholdReached: Boolean,
    val quality: EcgQuality
)

enum class HeartRateStatus { SUCCESS, NO_DATA_FLUSH, DEVICE_MOVING, UNRELIABLE, UNKNOWN }
enum class SpO2Status { SUCCESSFUL_MEASUREMENT, MEASUREMENT_FAILED, DEVICE_MOVING, FINGER_NOT_DETECTED, UNKNOWN }
enum class EcgQuality { GOOD, FAIR, POOR }
