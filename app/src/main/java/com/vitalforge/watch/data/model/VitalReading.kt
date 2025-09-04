package com.vitalforge.watch.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.vitalforge.watch.data.converter.VitalReadingConverters
import kotlinx.serialization.Serializable
import java.util.UUID

@Entity(tableName = "vital_readings")
@TypeConverters(VitalReadingConverters::class)
@Serializable
data class VitalReading(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String,
    val heartRate: Int? = null,
    val heartRateVariability: Float? = null,
    val systolicBP: Int? = null,
    val diastolicBP: Int? = null,
    val oxygenSaturation: Float? = null,
    val respiratoryRate: Int? = null,
    val skinTemperature: Float? = null,
    val stressLevel: Float? = null,
    val energyLevel: Float? = null,
    val dataSource: DataSource = DataSource.SAMSUNG_HEALTH,
    val accuracy: DataAccuracy = DataAccuracy.HIGH,
    val isProcessed: Boolean = false,
    val rawSensorData: String? = null
)
