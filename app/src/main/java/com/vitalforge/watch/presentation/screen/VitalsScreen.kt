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
fun VitalsScreen(vm: MainViewModel, navController: NavController) {
    val vitals by vm.latestVitalsList.collectAsState(initial = emptyList())
    LazyColumn {
        items(vitals) { r ->
            Text("Time: ${r.timestamp} HR: ${r.heartRate} bpm SpO₂: ${r.oxygenSaturation}%")
        }
    }
}
