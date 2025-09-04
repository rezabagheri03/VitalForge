package com.vitalforge.watch.data.ai

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AnomalyDetectorTest {

    private lateinit var detector: AnomalyDetector

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        detector = AnomalyDetector(context)
    }

    @Test
    fun detectAnomaly_normalData_returnsFalse() = runBlocking {
        val normal = floatArrayOf(60f, 50f, 98f, 0.1f)
        val (isAnomaly, _) = detector.detectAnomaly(normal)
        assertFalse(isAnomaly)
    }

    @Test
    fun detectAnomaly_extremeData_returnsTrue() = runBlocking {
        val extreme = floatArrayOf(200f, 0f, 50f, 5f)
        val (isAnomaly, _) = detector.detectAnomaly(extreme)
        assertTrue(isAnomaly)
    }
}
