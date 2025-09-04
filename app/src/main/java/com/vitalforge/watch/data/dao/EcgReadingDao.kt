package com.vitalforge.watch.data.dao

import androidx.room.*
import com.vitalforge.watch.data.model.EcgReading
import kotlinx.coroutines.flow.Flow

@Dao
interface EcgReadingDao {
    @Query("SELECT * FROM ecg_readings WHERE userId = :userId ORDER BY timestamp DESC LIMIT :limit")
    fun getRecentEcgReadings(userId: String, limit: Int = 50): Flow<List<EcgReading>>

    @Query("SELECT * FROM ecg_readings WHERE userId = :userId AND rhythm <> 'NORMAL_SINUS' ORDER BY timestamp DESC")
    fun getAbnormalEcgReadings(userId: String): Flow<List<EcgReading>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEcgReading(reading: EcgReading)

    @Query("DELETE FROM ecg_readings WHERE userId = :userId AND timestamp < :cutoff")
    suspend fun deleteOldReadings(userId: String, cutoff: Long)
}
