package com.vitalforge.companion.ble

import android.bluetooth.*
import android.bluetooth.BluetoothGatt.GATT_SUCCESS
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.vitalforge.companion.data.model.VitalReading
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@Singleton
class BluetoothSyncClient @Inject constructor(
    @ApplicationContext private val context: Context,
    private val repository: com.vitalforge.companion.data.VitalForgeRepository
) {
    private val adapter = (context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager).adapter
    private var gatt: BluetoothGatt? = null
    private val gson = Gson()

    fun startScan() {
        adapter.bluetoothLeScanner.startScan(scanCallback)
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            connect(result.device)
        }
    }

    private fun connect(device: BluetoothDevice) {
        gatt = device.connectGatt(context, false, gattCallback)
    }

    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
            if (status == GATT_SUCCESS && newState == BluetoothProfile.STATE_CONNECTED) {
                g.discoverServices()
            }
        }
        override fun onServicesDiscovered(g: BluetoothGatt, status: Int) {
            val service = g.getService(UUID.fromString("0000feed-0000-1000-8000-00805f9b34fb"))
            val char = service.getCharacteristic(UUID.fromString("0000beef-0000-1000-8000-00805f9b34fb"))
            g.setCharacteristicNotification(char, true)
            char.descriptors.forEach { it.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE; g.writeDescriptor(it) }
        }
        override fun onCharacteristicChanged(g: BluetoothGatt, characteristic: BluetoothGattCharacteristic) {
            val json = String(characteristic.value)
            val reading = gson.fromJson(json, VitalReading::class.java)
            CoroutineScope(Dispatchers.IO).launch {
                repository.insertVitalReading(reading)
            }
        }
    }
}
