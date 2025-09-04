package com.vitalforge.watch.data.security

import com.vitalforge.watch.data.dao.ConsentDao
import com.vitalforge.watch.data.model.ConsentRecord
import com.vitalforge.watch.data.model.ConsentType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConsentManager @Inject constructor(
    private val dao: ConsentDao
) {
    suspend fun hasConsent(type: ConsentType): Boolean =
        dao.get(type)?.granted ?: false

    suspend fun setConsent(type: ConsentType, granted: Boolean) {
        val record = ConsentRecord(type, granted, System.currentTimeMillis())
        dao.upsert(record)
    }
}
