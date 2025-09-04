package com.vitalforge.watch.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vitalforge.watch.presentation.screen.*

@Composable
fun VitalForgeNavigation(
    viewModel: com.vitalforge.watch.presentation.viewmodel.MainViewModel
) {
    val navController: NavHostController = rememberNavController()
    NavHost(navController, startDestination = "dashboard") {
        composable("dashboard") { DashboardScreen(viewModel, navController) }
        composable("vitals") { VitalsScreen(viewModel, navController) }
        composable("sleep") { SleepTrackingScreen(viewModel, navController) }
        composable("ecg") { EcgScreen(viewModel, navController) }
        composable("insights") { InsightsScreen(viewModel, navController) }
        composable("settings") { SettingsScreen(viewModel, navController) }
        composable("consent") { ConsentScreen(viewModel, navController) }
        composable("export") { ExportScreen(viewModel, navController) }
        composable("goals") { com.vitalforge.watch.presentation.screen.gamification.GoalsScreen(viewModel.gamificationViewModel) }
        composable("badges") { com.vitalforge.watch.presentation.screen.gamification.BadgesScreen(viewModel.gamificationViewModel) }
    }
}
