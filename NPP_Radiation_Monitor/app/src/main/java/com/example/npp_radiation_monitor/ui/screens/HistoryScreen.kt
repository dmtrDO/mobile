package com.example.npp_radiation_monitor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.npp_radiation_monitor.navigation.Screen
import com.example.npp_radiation_monitor.ui.components.MapScreenComponent
import com.example.npp_radiation_monitor.ui.viewmodel.RadiationViewModel

/**
 * Екран мапи та геолокації
 */
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: RadiationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Панель навігації
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { navController.navigate(Screen.MainScreen.route) },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Головна", fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Моніторинг", fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Інтерактивна мапа на весь екран із маркерами та GPS геолокацією
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(vertical = 4.dp)
        ) {
            MapScreenComponent(
                stations = uiState.energyStations,
                userLat = uiState.userLatitude,
                userLng = uiState.userLongitude,
                onStationClick = { station ->
                    viewModel.selectMapStation(station)
                }
            )
        }
    }
}
