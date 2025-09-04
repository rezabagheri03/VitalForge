package com.vitalforge.companion.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vital_readings")
data class VitalReading(
    @PrimaryKey val id: String,
    val timestamp: Long,
    val heartRate: Int,
    val oxygenSaturation: Float
)
