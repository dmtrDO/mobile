package com.example.npp_radiation_monitor.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.npp_radiation_monitor.ui.screens.HistoryScreen
import com.example.npp_radiation_monitor.ui.screens.MainScreen
import com.example.npp_radiation_monitor.ui.screens.RadiationScreen
import com.example.npp_radiation_monitor.ui.screens.StationFormScreen

/**
 * Головний граф навігації застосунку.
 * Управляє переходами між екранами: MainScreen, RadiationScreen, HistoryScreen та StationFormScreen.
 */
@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.MainScreen.route) {

        composable(route = Screen.MainScreen.route) {
            MainScreen(navController = navController)
        }

        composable(route = Screen.FormScreen.route) {
            StationFormScreen(navController = navController)
        }

        composable(
            route = Screen.RadiationScreen.route + "/{stationName}/{operatorName}",
            arguments = listOf(
                navArgument("stationName") {
                    type = NavType.StringType
                    defaultValue = "Моніторинг АЕС"
                },
                navArgument("operatorName") {
                    type = NavType.StringType
                    defaultValue = "Черговий оператор"
                }
            )
        ) { entry ->
            val stationName = entry.arguments?.getString("stationName")
                .orEmpty()
                .ifBlank { "Моніторинг АЕС" }
            val operatorName = entry.arguments?.getString("operatorName")
                .orEmpty()
                .ifBlank { "Черговий оператор" }

            RadiationScreen(
                navController = navController,
                operatorName = operatorName,
                stationName = stationName
            )
        }

        composable(route = Screen.HistoryScreen.route) {
            HistoryScreen(navController = navController)
        }
    }
}

