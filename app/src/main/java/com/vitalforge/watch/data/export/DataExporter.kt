package com.vitalforge.watch.data.export

import android.util.Base64
import com.vitalforge.watch.data.security.EncryptionManager
import com.vitalforge.watch.data.security.ConsentManager
import com.vitalforge.watch.data.dao.VitalReadingDao
import com.vitalforge.watch.data.model.ConsentType
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataExporter @Inject constructor(
    private val pdfGen: PdfGenerator,
    private val encryption: EncryptionManager,
    private val consent: ConsentManager,
    private val vitalDao: VitalReadingDao
) {
    suspend fun exportReport(userId: String): ByteArray? {
        if (!consent.hasConsent(ConsentType.TELEHEALTH_EXPORT)) return null
        val now = System.currentTimeMillis()
        val start = now - TimeUnit.DAYS.toMillis(30)
        val readings = vitalDao.getReadingsInRange(userId, start, now).first()
        val pdf = pdfGen.generateMonthlyReport(userId, readings)
        val b64 = Base64.encodeToString(pdf, Base64.NO_WRAP)
        return encryption.encryptData(b64).encryptedData
    }
}
