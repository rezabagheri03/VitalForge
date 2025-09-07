package com.vitalforge.watch.health

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.wear.health.services.HealthServices
import androidx.wear.health.services.client.MeasureClient
import androidx.wear.health.services.client.PassiveMonitoringClient
import androidx.wear.health.services.client.capability.DeviceCapabilitiesClient
import androidx.wear.health.services.client.data.DataType
import androidx.wear.health.services.client.records.HeartRateRecord

class HealthServicesManager(context: Context) {

    private val measureClient: MeasureClient =
        HealthServices.getClient(context).measureClient
    private val _heartRateData = MutableLiveData<HeartRateRecord>()
    val heartRateData: LiveData<HeartRateRecord> = _heartRateData

    init {
        HealthServices.getClient(context).deviceCapabilitiesClient.getCapabilities()
            .addOnSuccessListener { caps: DeviceCapabilitiesClient.Capabilities ->
                if (caps.supportsHeartRateMeasurement()) {
                    startHeartRate()
                }
            }
    }

    private fun startHeartRate() {
        measureClient.addDataCallback(
            DataType.HEART_RATE_BPM
        ) { dataPoints ->
            dataPoints.forEach { record ->
                _heartRateData.postValue(record as HeartRateRecord)
            }
        }
    }

    fun stopHeartRate() {
        measureClient.removeDataCallback(DataType.HEART_RATE_BPM)
    }
}
