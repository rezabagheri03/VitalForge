package com.vitalforge.watch.presentation.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.vitalforge.watch.presentation.viewmodel.MainViewModel

@Composable
fun EcgScreen(vm: MainViewModel, navController: NavController) {
    val ecgReadings by vm.abnormalEcgList.collectAsState(initial = emptyList())
    LazyColumn {
        items(ecgReadings) { reading ->
            Text("Time: ${reading.timestamp} HR: ${reading.heartRate} Rhythm: ${reading.rhythm}")
            Text("Confidence: ${reading.confidence} Artifact level: ${reading.artifactLevel}")
        }
    }
}
