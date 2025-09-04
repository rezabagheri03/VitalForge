package com.vitalforge.companion.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.vitalforge.companion.data.model.VitalReading
import kotlinx.coroutines.flow.Flow

@Dao
interface VitalReadingDao {
    @Query("SELECT * FROM vital_readings ORDER BY timestamp DESC")
    fun getAll(): Flow<List<VitalReading>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(reading: VitalReading)
}
