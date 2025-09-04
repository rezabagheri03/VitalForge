package com.vitalforge.watch.data.health

import androidx.health.services.client.PassiveListenerService
import androidx.health.services.client.data.DataPointContainer
import androidx.health.services.client.data.DataType
import com.vitalforge.watch.data.model.HealthServicesData
import com.vitalforge.watch.data.model.DataSource
import com.vitalforge.watch.data.model.DataAccuracy
import com.vitalforge.watch.data.repository.VitalForgeRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class HealthServicesPassiveDataService : PassiveListenerService() {

    @Inject lateinit var repository: VitalForgeRepository

    override fun onNewDataPointsReceived(dataPoints: DataPointContainer) {
        GlobalScope.launch {
            dataPoints.forEach { dp ->
                val hr = dp.getData(DataType.HEART_RATE_BPM).lastOrNull()?.value?.toInt()
                hr?.let {
                    val vs = HealthServicesData(
                        heartRate = it,
                        timestamp = dp.timeDurationFromBoot.toEpochMilli(),
                        dataSource = DataSource.HEALTH_SERVICES,
                        accuracy = DataAccuracy.HIGH
                    )
                    repository.insertVitalReading(vs.toVitalReading())
                }
            }
        }
    }
}
