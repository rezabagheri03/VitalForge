package com.vitalforge.companion.ui

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.vitalforge.companion.data.VitalForgeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun DashboardScreen(viewModel: CompanionViewModel) {
    val list by viewModel.readings.collectAsState(initial = emptyList())
    LazyColumn {
        items(list) { r ->
            Text("Time: ${r.timestamp} HR: ${r.heartRate}")
        }
    }
}
