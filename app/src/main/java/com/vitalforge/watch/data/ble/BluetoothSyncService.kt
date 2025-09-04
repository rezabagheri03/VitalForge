package com.vitalforge.watch.data.ble

import android.app.Service
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.google.gson.Gson
import com.vitalforge.watch.data.model.VitalReading
import com.vitalforge.watch.data.repository.VitalForgeRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class BluetoothSyncService @Inject constructor(
    private val gattServer: GattServer,
    private val repository: VitalForgeRepository
) : Service() {
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val gson = Gson()

    companion object { private const val TAG = "BluetoothSyncSvc" }

    override fun onCreate() {
        super.onCreate()
        gattServer.start()
        scope.launch {
            repository.getUnprocessedReadings().forEach { reading ->
                syncReading(reading)
                // mark as processed (not shown)
            }
        }
    }

    private suspend fun repository.getUnprocessedReadings(): List<VitalReading> =
        repository.vitalDao.getUnprocessedReadings()

    private fun syncReading(reading: VitalReading) {
        val json = gson.toJson(reading)
        val device: BluetoothDevice = findConnectedDevice() ?: return
        gattServer.notify(device, json.toByteArray())
    }

    private fun findConnectedDevice(): BluetoothDevice? {
        // Return the first bonded device (for simplicity)
        return gattServer.manager.adapter.bondedDevices.firstOrNull()
    }

    override fun onDestroy() {
        super.onDestroy()
        gattServer.stop()
        scope.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
