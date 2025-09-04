package com.vitalforge.watch.data.dao

import androidx.room.*
import com.vitalforge.watch.data.model.UserPreference
import kotlinx.coroutines.flow.Flow

@Dao
interface UserPreferenceDao {
    @Query("SELECT * FROM user_preferences")
    fun getAll(): Flow<List<UserPreference>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(pref: UserPreference)

    @Delete
    suspend fun delete(pref: UserPreference)
}
