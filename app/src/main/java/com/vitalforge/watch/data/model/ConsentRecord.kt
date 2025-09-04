package com.vitalforge.watch.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "consent_records")
@Serializable
data class ConsentRecord(
    @PrimaryKey val type: ConsentType,
    val granted: Boolean,
    val timestamp: Long
)
