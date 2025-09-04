package com.vitalforge.companion.data

import com.vitalforge.companion.data.model.VitalReading
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VitalForgeRepository @Inject constructor(
    private val dao: VitalReadingDao
) {
    fun getAllReadings(): Flow<List<VitalReading>> = dao.getAll()
    suspend fun insertVitalReading(reading: VitalReading) = dao.insert(reading)
}
