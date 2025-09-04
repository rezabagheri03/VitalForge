package com.vitalforge.watch.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vitalforge.watch.data.dao.*
import com.vitalforge.watch.data.model.*

@Database(
    entities = [
        VitalReading::class,
        SleepSession::class,
        EcgReading::class,
        AiInsight::class,
        UserPreference::class,
        ConsentRecord::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VitalForgeDatabase : RoomDatabase() {
    abstract fun vitalReadingDao(): VitalReadingDao
    abstract fun sleepSessionDao(): SleepSessionDao
    abstract fun ecgReadingDao(): EcgReadingDao
    abstract fun aiInsightDao(): AiInsightDao
    abstract fun userPreferenceDao(): UserPreferenceDao
    abstract fun consentDao(): ConsentDao
}
