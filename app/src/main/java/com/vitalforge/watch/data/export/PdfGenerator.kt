package com.vitalforge.watch.data.export

import android.content.Context
import com.itextpdf.text.Document
import com.itextpdf.text.Paragraph
import com.itextpdf.text.pdf.PdfWriter
import com.vitalforge.watch.data.model.VitalReading
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PdfGenerator @Inject constructor(
    private val context: Context
) {
    fun generateMonthlyReport(userId: String, readings: List<VitalReading>): ByteArray {
        val document = Document()
        val output = ByteArrayOutputStream()
        PdfWriter.getInstance(document, output)
        document.open()
        document.add(Paragraph("VitalForge Monthly Report"))
        document.add(Paragraph("User: $userId"))
        document.add(Paragraph("Date: ${System.currentTimeMillis()}"))
        document.add(Paragraph(" "))
        readings.forEach { r ->
            document.add(Paragraph("Time: ${r.timestamp}"))
            document.add(Paragraph("HR: ${r.heartRate} bpm, SpO₂: ${r.oxygenSaturation}%"))
            document.add(Paragraph("Sleep Quality: ${r.sleepQuality ?: "N/A"}"))
            document.add(Paragraph("-----"))
        }
        document.close()
        return output.toByteArray()
    }
}
