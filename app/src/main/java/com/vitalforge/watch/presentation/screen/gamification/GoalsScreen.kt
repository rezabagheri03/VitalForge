package com.vitalforge.watch.presentation.screen.gamification

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vitalforge.watch.presentation.viewmodel.GamificationViewModel

@Composable
fun GoalsScreen(vm: GamificationViewModel) {
    val goals by vm.goals.collectAsState()
    LazyColumn {
        items(goals) { goal ->
            Column(modifier = Modifier.padding(8.dp)) {
                Text(goal.title)
                LinearProgressIndicator(progress = goal.progress / goal.target.toFloat(), modifier = Modifier.height(4.dp))
                Text("${goal.progress}/${goal.target} ${goal.unit}")
            }
        }
    }
}
