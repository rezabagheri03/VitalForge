package com.vitalforge.companion.ble

import android.app.Service
import android.bluetooth.*
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.util.*

class CompanionBleService : Service() {

    private val binder = LocalBinder()

    // LiveData for incoming BLE data (e.g., heart rate or vitals)
    private val _incomingData = MutableLiveData<String>()
    val incomingData: LiveData<String> get() = _incomingData

    private var bluetoothGatt: BluetoothGatt? = null

    // Replace with your device’s service and characteristic UUIDs
    private val SERVICE_UUID: UUID = UUID.fromString("0000180d-0000-1000-8000-00805f9b34fb")
    private val CHAR_UUID: UUID = UUID.fromString("00002a37-0000-1000-8000-00805f9b34fb")

    inner class LocalBinder : Binder() {
        val service: CompanionBleService get() = this@CompanionBleService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onUnbind(intent: Intent): Boolean {
        bluetoothGatt?.disconnect()
        bluetoothGatt?.close()
        bluetoothGatt = null
        return super.onUnbind(intent)
    }

    /** Call this to connect to a BLE device by address */
    fun connectToDevice(device: BluetoothDevice) {
        bluetoothGatt = device.connectGatt(this, false, gattCallback)
    }

    /** BLE callback to handle connection and data */
    private val gattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                _incomingData.postValue("Disconnected")
            }
        }

        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            val characteristic = gatt.getService(SERVICE_UUID)
                ?.getCharacteristic(CHAR_UUID)
            characteristic?.let {
                gatt.setCharacteristicNotification(it, true)
                val descriptor = it.getDescriptor(
                    UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
                )
                descriptor?.apply {
                    value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                    gatt.writeDescriptor(this)
                }
            }
        }

        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            val dataBytes = characteristic.value
            val dataString = dataBytes.joinToString(separator = ",") { it.toString() }
            _incomingData.postValue(dataString)
        }
    }
}
