package com.vitalforge.watch.data.dao

import androidx.room.*
import com.vitalforge.watch.data.model.AiInsight
import kotlinx.coroutines.flow.Flow

@Dao
interface AiInsightDao {
    @Query("SELECT * FROM ai_insights WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAllInsights(userId: String): Flow<List<AiInsight>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInsight(insight: AiInsight)

    @Update
    suspend fun updateInsight(insight: AiInsight)

    @Query("DELETE FROM ai_insights WHERE expiresAt IS NOT NULL AND expiresAt < :now")
    suspend fun deleteExpiredInsights(now: Long)
}
