package com.vitalforge.companion.ui

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.material3.Text
import androidx.compose.ui.platform.ComposeView
import com.vitalforge.companion.ble.CompanionBleService

class MainActivity : ComponentActivity() {

    private var bleService: CompanionBleService? = null
    private var bound = false

    private val permissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            if (perms.values.all { it }) {
                bindBleService()
            } else {
                finish() // Permissions denied
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_companion)
        requestBluetoothPermissions()
    }

    private fun requestBluetoothPermissions() {
        val needed = arrayOf(
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val missing = needed.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isEmpty()) {
            bindBleService()
        } else {
            permissionRequest.launch(missing.toTypedArray())
        }
    }

    private fun bindBleService() {
        Intent(this, CompanionBleService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, binder: IBinder) {
            bleService = (binder as CompanionBleService.LocalBinder).service
            bound = true
            observeBleData()
        }

        override fun onServiceDisconnected(name: ComponentName) {
            bound = false
            bleService = null
        }
    }

    private fun observeBleData() {
        // Observe LiveData or callbacks from the BLE service and update UI
        bleService?.incomingData?.observe(this) { data ->
            findViewById<ComposeView>(R.id.compose_view_companion).setContent {
                Text(text = "Received: $data")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (bound) {
            unbindService(serviceConnection)
            bound = false
        }
    }
}
