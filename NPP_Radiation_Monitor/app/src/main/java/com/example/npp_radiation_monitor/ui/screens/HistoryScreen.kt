package com.example.npp_radiation_monitor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.npp_radiation_monitor.data.model.RadiationReading
import com.example.npp_radiation_monitor.data.model.RadiationStatus
import com.example.npp_radiation_monitor.navigation.Screen
import com.example.npp_radiation_monitor.ui.components.HistoryItem
import com.example.npp_radiation_monitor.ui.viewmodel.RadiationViewModel

/**
 * Екран історії вимірювань (HistoryScreen).
 * Відображає список усіх збережених вимірювань радіаційного фону з локальної бази даних Room.
 */
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: RadiationViewModel = viewModel()
) {
    // Отримуємо реактивний список записів із локальної бази даних Room
    val roomReadings by viewModel.allReadingsFromRoom.collectAsState(initial = emptyList())

    val history = roomReadings.map { entity ->
        RadiationReading(
            id = entity.id,
            value = entity.value,
            timestamp = entity.timestamp,
            status = RadiationStatus.fromValue(entity.value)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Кнопка повернення на головний екран
        Button(
            onClick = { navController.navigate(Screen.MainScreen.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Повернутися на головну", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.padding(8.dp))

        // Кнопка повернення на екран моніторингу
        Button(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Повернутися до моніторингу радіації", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.padding(8.dp))

        // Заголовок екрана історії
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = "Історія вимірювань",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Список усіх вимірювань із Room БД
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(history, key = { it.id }) { item ->
                HistoryItem(reading = item)
            }
        }
    }
}