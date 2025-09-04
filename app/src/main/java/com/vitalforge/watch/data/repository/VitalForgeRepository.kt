package com.vitalforge.watch.data.repository

import com.vitalforge.watch.data.dao.*
import com.vitalforge.watch.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VitalForgeRepository @Inject constructor(
    private val vitalDao: VitalReadingDao,
    private val sleepDao: SleepSessionDao,
    private val ecgDao: EcgReadingDao,
    private val insightDao: AiInsightDao,
    private val prefDao: UserPreferenceDao,
    private val consentDao: ConsentDao
) {
    fun getLatestVitals(userId: String, limit: Int): Flow<List<VitalReading>> =
        vitalDao.getLatestReadings(userId, limit)

    fun getRecentSleep(userId: String, limit: Int): Flow<List<SleepSession>> =
        sleepDao.getRecentSleepSessions(userId, limit)

    fun getAbnormalEcg(userId: String): Flow<List<EcgReading>> =
        ecgDao.getAbnormalEcgReadings(userId)

    fun getAllInsights(userId: String): Flow<List<AiInsight>> =
        insightDao.getAllInsights(userId)

    suspend fun insertVital(reading: VitalReading) =
        vitalDao.insertReading(reading)

    suspend fun insertSleep(session: SleepSession) =
        sleepDao.insertSleepSession(session)

    suspend fun insertEcg(reading: EcgReading) =
        ecgDao.insertEcgReading(reading)

    suspend fun insertInsight(insight: AiInsight) =
        insightDao.insertInsight(insight)

    suspend fun upsertPreference(pref: UserPreference) =
        prefDao.upsert(pref)

    suspend fun setConsent(type: ConsentType, granted: Boolean) =
        consentDao.upsert(ConsentRecord(type, granted, System.currentTimeMillis()))

    suspend fun hasConsent(type: ConsentType): Boolean =
        consentDao.get(type)?.granted ?: false

    fun getDashboardData(userId: String): Flow<DashboardData> =
        combine(
            getLatestVitals(userId,1),
            getRecentSleep(userId,1),
            getAllInsights(userId)
        ) { vitals, sleep, insights ->
            DashboardData(
                latestVitals = vitals.firstOrNull(),
                lastSleep = sleep.firstOrNull(),
                activeInsights = insights
            )
        }
}
