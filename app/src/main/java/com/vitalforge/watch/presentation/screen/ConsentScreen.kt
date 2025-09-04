package com.vitalforge.watch.presentation.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.vitalforge.watch.presentation.viewmodel.MainViewModel

@Composable
fun ConsentScreen(vm: MainViewModel, navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    val monitoringConsent by vm.hasMonitoringConsent.collectAsState()

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Allow health data collection")
        Switch(
            checked = monitoringConsent,
            onCheckedChange = { vm.updateMonitoringConsent(it) }
        )
        Button(onClick = { showDialog = true }) {
            Text("Telehealth Export Consent")
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Enable Telehealth Export?") },
            text = { Text("Allow secure export of reports to clinician apps.") },
            confirmButton = {
                Button(onClick = {
                    vm.updateExportConsent(true)
                    showDialog = false
                }) { Text("Yes") }
            },
            dismissButton = {
                Button(onClick = {
                    vm.updateExportConsent(false)
                    showDialog = false
                }) { Text("No") }
            }
        )
    }
}
