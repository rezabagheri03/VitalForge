package com.vitalforge.watch.data.dao

import androidx.room.*
import com.vitalforge.watch.data.model.VitalReading
import kotlinx.coroutines.flow.Flow

@Dao
interface VitalReadingDao {
    @Query("SELECT * FROM vital_readings WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    fun getLatestReadings(userId: String, limit: Int = 100): Flow<List<VitalReading>>

    @Query("SELECT * FROM vital_readings WHERE userId = :userId AND timestamp BETWEEN :start AND :end ORDER BY timestamp DESC")
    fun getReadingsInRange(userId: String, start: Long, end: Long): Flow<List<VitalReading>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: VitalReading)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadings(readings: List<VitalReading>>

    @Query("DELETE FROM vital_readings WHERE userId = :userId AND timestamp < :cutoff")
    suspend fun deleteOldReadings(userId: String, cutoff: Long)

    @Query("SELECT * FROM vital_readings WHERE isProcessed = 0 ORDER BY timestamp ASC")
    suspend fun getUnprocessedReadings(): List<VitalReading>
}
