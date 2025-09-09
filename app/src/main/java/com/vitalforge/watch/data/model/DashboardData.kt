package com.vitalforge.watch.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DashboardData(
    val latestVitals: VitalReading?,
    val lastSleep: SleepSession?,
    val activeInsights: List<AiInsight>
)