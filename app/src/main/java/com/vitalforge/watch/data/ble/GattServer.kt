package com.vitalforge.watch.data.ble

import android.bluetooth.*
import android.bluetooth.BluetoothGattCharacteristic.*
import android.bluetooth.BluetoothGattService.SERVICE_TYPE_PRIMARY
import android.content.Context
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GattServer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val manager: BluetoothManager =
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private val adapter: BluetoothAdapter = manager.adapter
    private var server: BluetoothGattServer? = null

    companion object {
        private const val TAG = "GattServer"
        val SERVICE_UUID: UUID = UUID.fromString("0000feed-0000-1000-8000-00805f9b34fb")
        val CHARACTERISTIC_UUID: UUID = UUID.fromString("0000beef-0000-1000-8000-00805f9b34fb")
    }

    fun start() {
        server = manager.openGattServer(context, gattCallback)
        val service = BluetoothGattService(SERVICE_UUID, SERVICE_TYPE_PRIMARY)
        val characteristic = BluetoothGattCharacteristic(
            CHARACTERISTIC_UUID,
            PROPERTY_WRITE or PROPERTY_NOTIFY,
            PERMISSION_WRITE
        )
        service.addCharacteristic(characteristic)
        server?.addService(service)
        Log.d(TAG, "GATT server started")
    }

    private val gattCallback = object : BluetoothGattServerCallback() {
        override fun onConnectionStateChange(device: BluetoothDevice, status: Int, newState: Int) {
            super.onConnectionStateChange(device, status, newState)
            Log.d(TAG, "Connection state changed: $device status=$status newState=$newState")
        }

        override fun onCharacteristicWriteRequest(
            device: BluetoothDevice,
            requestId: Int,
            characteristic: BluetoothGattCharacteristic,
            preparedWrite: Boolean,
            responseNeeded: Boolean,
            offset: Int,
            value: ByteArray
        ) {
            super.onCharacteristicWriteRequest(device, requestId, characteristic, preparedWrite, responseNeeded, offset, value)
            Log.d(TAG, "Write request from $device: ${String(value)}")
            if (responseNeeded) {
                server?.sendResponse(device, requestId, BluetoothGatt.GATT_SUCCESS, 0, null)
            }
            // Process received data (e.g., JSON payload)
        }
    }

    fun notify(device: BluetoothDevice, data: ByteArray) {
        val characteristic = server?.getService(SERVICE_UUID)
            ?.getCharacteristic(CHARACTERISTIC_UUID) ?: return
        characteristic.value = data
        server?.notifyCharacteristicChanged(device, characteristic, false)
    }

    fun stop() {
        server?.close()
        server = null
        Log.d(TAG, "GATT server stopped")
    }
}
