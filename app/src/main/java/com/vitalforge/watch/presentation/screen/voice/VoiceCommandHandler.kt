package com.vitalforge.watch.presentation.screen.voice

import androidx.navigation.NavController

class VoiceCommandHandler(private val navController: NavController) {
    fun handle(command: String) {
        when {
            command.contains("show goals", ignoreCase = true) -> navController.navigate("goals")
            command.contains("show badges", ignoreCase = true) -> navController.navigate("badges")
            command.contains("start monitoring", ignoreCase = true) -> navController.navigate("dashboard")
            command.contains("export report", ignoreCase = true) -> navController.navigate("export")
        }
    }
}
