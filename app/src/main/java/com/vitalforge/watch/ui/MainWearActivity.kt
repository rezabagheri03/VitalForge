package com.vitalforge.watch.ui

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat
import androidx.wear.health.services.HealthServices
import com.vitalforge.watch.health.HealthServicesManager

class MainWearActivity : ComponentActivity() {

    private lateinit var healthServicesManager: HealthServicesManager

    private val permissionRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { perms ->
            if (perms.values.all { it }) {
                initHealthServices()
            } else {
                finish() // Permissions denied
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_wear)

        healthServicesManager = HealthServicesManager(this)

        requestRuntimePermissions()
    }

    private fun requestRuntimePermissions() {
        val needed = arrayOf(
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACTIVITY_RECOGNITION,
            Manifest.permission.BLUETOOTH_CONNECT,
            Manifest.permission.BLUETOOTH_SCAN
        )
        val missing = needed.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missing.isEmpty()) {
            initHealthServices()
        } else {
            permissionRequest.launch(missing.toTypedArray())
        }
    }

    private fun initHealthServices() {
        // Ensure Health Services client is available
        HealthServices.getClient(this).deviceCapabilitiesClient.getCapabilities()
            .addOnSuccessListener { caps ->
                if (caps.supportsHeartRateMeasurement()) {
                    startHeartRateMonitoring()
                } else {
                    // Show UI indicating no heart-rate support
                    showNoSupportMessage()
                }
            }
    }

    private fun startHeartRateMonitoring() {
        // Observe heart rate LiveData and update Compose UI
        findViewById<ComposeView>(R.id.compose_view_wear).setContent {
            val hrValue = healthServicesManager.heartRateData.collectAsState(initial = null)
            LaunchedEffect(hrValue.value) {
                // no-op, triggers recomposition
            }
            Text(
                text = hrValue.value?.toString() ?: "Heart Rate: --",
                modifier = Modifier.fillMaxSize(),
                style = MaterialTheme.typography.displayLarge
            )
        }
        // Start sampling
        healthServicesManager.startHeartRate()
    }

    private fun showNoSupportMessage() {
        findViewById<ComposeView>(R.id.compose_view_wear).setContent {
            Text(
                text = "Heart Rate Not Supported",
                modifier = Modifier.fillMaxSize(),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        healthServicesManager.stopHeartRate()
    }
}
