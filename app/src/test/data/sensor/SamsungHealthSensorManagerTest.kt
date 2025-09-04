package com.vitalforge.watch.data.sensor

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SamsungHealthSensorManagerTest {

    private lateinit var manager: SamsungHealthSensorManager

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        manager = SamsungHealthSensorManager(context)
    }

    @Test
    fun initialize_connectsSuccessfully() = runBlocking {
        val result = manager.initialize()
        assertTrue(result)
    }
}
