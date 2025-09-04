package com.vitalforge.watch.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.vitalforge.watch.data.converter.AiInsightConverters
import kotlinx.serialization.Serializable
import java.util.UUID

@Entity(tableName = "ai_insights")
@TypeConverters(AiInsightConverters::class)
@Serializable
data class AiInsight(
    @PrimaryKey val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val timestamp: Long,
    val insightType: InsightType,
    val severity: InsightSeverity,
    val title: String,
    val description: String,
    val recommendations: List<String>,
    val confidence: Float,
    val dataPoints: List<String>,
    val isActionable: Boolean,
    val expiresAt: Long? = null,
    val userFeedback: UserFeedback? = null,
    val metadata: Map<String, String> = emptyMap()
)
