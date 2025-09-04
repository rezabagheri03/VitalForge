package com.vitalforge.watch.data.dao

import androidx.room.*
import com.vitalforge.watch.data.model.SleepSession
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepSessionDao {
    @Query("SELECT * FROM sleep_sessions WHERE userId = :userId ORDER BY startTime DESC LIMIT :limit")
    fun getRecentSleepSessions(userId: String, limit: Int = 30): Flow<List<SleepSession>>

    @Query("SELECT * FROM sleep_sessions WHERE userId = :userId ORDER BY startTime DESC LIMIT 1")
    suspend fun getLastSleepSession(userId: String): SleepSession?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleepSession(session: SleepSession)

    @Update
    suspend fun updateSleepSession(session: SleepSession)

    @Query("DELETE FROM sleep_sessions WHERE userId = :userId AND startTime < :cutoff")
    suspend fun deleteOldSessions(userId: String, cutoff: Long)
}
