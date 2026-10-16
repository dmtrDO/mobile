package com.example.npp_radiation_monitor.navigation

sealed class Screen(val route: String) {
    object MainScreen : Screen("main_screen")
    object HistoryScreen : Screen("history_screen")
    object FormScreen : Screen("form_screen")
    object RadiationScreen : Screen("radiation_screen") {
        fun createRoute(
            stationName: String = "Моніторинг АЕС",
            operatorName: String = "Черговий оператор"
        ): String {
            val safeStation = stationName.ifBlank { "Моніторинг АЕС" }
            val safeOperator = operatorName.ifBlank { "Черговий оператор" }
            return "radiation_screen/$safeStation/$safeOperator"
        }
    }
}


