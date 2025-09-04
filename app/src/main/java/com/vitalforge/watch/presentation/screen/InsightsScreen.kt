package com.vitalforge.watch.presentation.screen

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import com.vitalforge.watch.presentation.viewmodel.MainViewModel

@Composable
fun InsightsScreen(vm: MainViewModel, navController: NavController) {
    val insights by vm.allInsights.collectAsState(initial = emptyList())
    LazyColumn {
        items(insights) { insight ->
            Card(modifier = androidx.compose.ui.Modifier.fillMaxWidth().padding(8.dp)) {
                Text(insight.title)
                Text(insight.description)
                Text("Severity: ${insight.severity} Confidence: ${insight.confidence}")
            }
        }
    }
}
