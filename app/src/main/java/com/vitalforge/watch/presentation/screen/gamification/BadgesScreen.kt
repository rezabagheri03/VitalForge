package com.vitalforge.watch.presentation.screen.gamification

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Chip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import com.vitalforge.watch.presentation.viewmodel.GamificationViewModel

@Composable
fun BadgesScreen(vm: GamificationViewModel) {
    val badges by vm.badges.collectAsState()
    if (badges.isEmpty()) {
        Text("No badges earned yet")
    } else {
        LazyColumn {
            items(badges) { badge ->
                Column {
                    Chip(onClick = {}, label = { Text(badge.name) }, icon = {
                        Icon(painter = painterResource(badge.iconRes), contentDescription = null)
                    })
                    Text(badge.description)
                }
            }
        }
    }
}
