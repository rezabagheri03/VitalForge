package com.vitalforge.watch.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.vitalforge.watch.presentation.viewmodel.MainViewModel

@Composable
fun SettingsScreen(vm: MainViewModel, navController: NavController) {
    val monitoringConsent by vm.hasMonitoringConsent.collectAsState()
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Health Monitoring Consent")
        Switch(
            checked = monitoringConsent,
            onCheckedChange = { vm.updateMonitoringConsent(it) }
        )
        Text("Version: 1.0.0")
    }
}
