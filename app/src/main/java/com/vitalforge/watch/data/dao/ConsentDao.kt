package com.vitalforge.watch.data.dao

import androidx.room.*
import com.vitalforge.watch.data.model.ConsentRecord
import com.vitalforge.watch.data.model.ConsentType
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsentDao {
    @Query("SELECT * FROM consent_records WHERE type = :type")
    fun get(type: ConsentType): Flow<ConsentRecord?>

    @Query("SELECT * FROM consent_records WHERE type = :type")
    suspend fun getSync(type: ConsentType): ConsentRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(consent: ConsentRecord)

    @Query("SELECT * FROM consent_records")
    fun getAll(): Flow<List<ConsentRecord>>

    @Query("DELETE FROM consent_records WHERE type = :type")
    suspend fun delete(type: ConsentType)

    @Query("DELETE FROM consent_records")
    suspend fun deleteAll()
}
