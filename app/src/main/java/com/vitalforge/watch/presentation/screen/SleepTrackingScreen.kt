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
fun SleepTrackingScreen(vm: MainViewModel, navController: NavController) {
    val sessions by vm.recentSleepSessions.collectAsState(initial = emptyList())
    LazyColumn {
        items(sessions) { s ->
            Text("Start: ${s.startTime} End: ${s.endTime} Quality: ${s.sleepQuality}")
        }
    }
}
