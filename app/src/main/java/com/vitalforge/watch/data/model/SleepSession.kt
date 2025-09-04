package com.vitalforge.watch.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.vitalforge.watch.data.converter.SleepSessionConverters
import kotlinx.serialization.Serializable
import java.util.UUID

@Entity(tableName = "sleep_sessions")
@TypeConverters(SleepSessionConverters::class)
@Serializable
data class SleepSession(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val startTime: Long,
    val endTime: Long,
    val totalSleepMinutes: Int,
    val sleepStages: List<SleepStage>,
    val sleepQuality: Float,
    val restfulnessScore: Float,
    val awakeDurations: List<AwakeInterval>,
    val averageHeartRate: Int? = null,
    val heartRateVariability: Float? = null,
    val oxygenLevels: List<Float> = emptyList(),
    val movementCount: Int = 0,
    val environmentalFactors: EnvironmentalData? = null
)
