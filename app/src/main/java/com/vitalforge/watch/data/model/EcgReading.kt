package com.vitalforge.watch.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.vitalforge.watch.data.converter.EcgReadingConverters
import kotlinx.serialization.Serializable
import java.util.UUID

@Entity(tableName = "ecg_readings")
@TypeConverters(EcgReadingConverters::class)
@Serializable
data class EcgReading(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val timestamp: Long,
    val duration: Int,
    val samplingRate: Int,
    val leadType: EcgLeadType = EcgLeadType.LEAD_I,
    val rawSignal: FloatArray,
    val heartRate: Int,
    val rhythm: CardiacRhythm,
    val qrsComplexes: List<QrsComplex>,
    val analysisFlags: List<AnalysisFlag>,
    val confidence: Float,
    val artifactLevel: Float,
    val clinicalNotes: String? = null
)
