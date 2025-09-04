package com.vitalforge.watch.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vitalforge.watch.data.ai.AnomalyDetector
import com.vitalforge.watch.data.export.DataExporter
import com.vitalforge.watch.data.health.HealthServicesManager
import com.vitalforge.watch.data.repository.VitalForgeRepository
import com.vitalforge.watch.presentation.screen.haptic.HapticPatterns
import com.vitalforge.watch.presentation.screen.voice.VoiceCommandHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UiState(
    val latestVitals: com.vitalforge.watch.data.model.VitalReading? = null,
    val hasMonitoringConsent: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: VitalForgeRepository,
    private val healthServicesManager: HealthServicesManager,
    private val anomalyDetector: AnomalyDetector,
    private val dataExporter: DataExporter
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    val latestVitalsList = repository.getLatestVitals("default_user", 100)
    val recentSleepSessions = repository.getRecentSleep("default_user", 30)
    val abnormalEcgList = repository.getAbnormalEcg("default_user")
    val allInsights = repository.getAllInsights("default_user")
    val hasMonitoringConsent = MutableStateFlow(false)

    val gamificationViewModel = GamificationViewModel()

    fun initialize() {
        viewModelScope.launch {
            hasMonitoringConsent.value = repository.hasConsent(com.vitalforge.watch.data.model.ConsentType.HEALTH_MONITORING)
            healthServicesManager.initialize()
            if (hasMonitoringConsent.value) {
                healthServicesManager.startPassiveMonitoring()
                healthServicesManager.startHeartRateMeasurement()
                healthServicesManager.startSpO2Measurement()
            }
        }
        subscribeVitals()
    }

    private fun subscribeVitals() {
        viewModelScope.launch {
            latestVitalsList.collect { vitals ->
                _uiState.update { it.copy(latestVitals = vitals.firstOrNull()) }
                vitals.firstOrNull()?.let { reading ->
                    val features = floatArrayOf(
                        reading.heartRate?.toFloat() ?: 0f,
                        reading.heartRateVariability ?: 0f,
                        reading.oxygenSaturation ?: 0f,
                        reading.stressLevel ?: 0f
                    )
                    val (isAnomaly, error) = anomalyDetector.detectAnomaly(features)
                    if (isAnomaly) {
                        HapticPatterns.play(ApplicationProvider.context, HapticPatterns.Alert)
                    }
                }
            }
        }
    }

    fun updateMonitoringConsent(granted: Boolean) {
        viewModelScope.launch {
            repository.setConsent(com.vitalforge.watch.data.model.ConsentType.HEALTH_MONITORING, granted)
            hasMonitoringConsent.value = granted
            if (granted) initialize() else healthServicesManager.stopAllMeasurements()
        }
    }

    fun updateExportConsent(granted: Boolean) {
        viewModelScope.launch {
            repository.setConsent(com.vitalforge.watch.data.model.ConsentType.TELEHEALTH_EXPORT, granted)
        }
    }

    fun exportReport(): ByteArray? {
        var result: ByteArray? = null
        viewModelScope.launch {
            result = dataExporter.exportReport("default_user")
        }
        return result
    }

    fun onPermissionsResult(granted: Boolean) {
        if (granted) initialize()
    }

    fun onAppResume() { /* resume tracking if needed */ }
    fun onAppPause() { /* pause tracking to save battery */ }
}
