package com.vitalforge.watch.presentation.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vitalforge.watch.presentation.viewmodel.MainViewModel

@Composable
fun DashboardScreen(vm: MainViewModel, navController: NavController) {
    val uiState by vm.uiState.collectAsState()
    Column(
        Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Heart Rate: ${uiState.latestVitals?.heartRate ?: "--"} bpm")
        Spacer(Modifier.height(8.dp))
        Text("SpO₂: ${uiState.latestVitals?.oxygenSaturation ?: "--"}%")
        Spacer(Modifier.height(16.dp))
        Button(onClick = { navController.navigate("vitals") }) { Text("View Vitals") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { navController.navigate("sleep") }) { Text("View Sleep") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { navController.navigate("insights") }) { Text("Insights") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { navController.navigate("goals") }) { Text("Goals") }
    }
}
