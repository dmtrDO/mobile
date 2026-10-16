package com.example.npp_radiation_monitor.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.npp_radiation_monitor.navigation.Screen
import com.example.npp_radiation_monitor.ui.components.StatusCard
import com.example.npp_radiation_monitor.ui.viewmodel.RadiationViewModel

/**
 * Екран моніторингу радіації (RadiationScreen).
 * Відображає поточні показники радіаційного фону, дозволяє генерувати
 * нові вимірювання за допомогою RadiationViewModel та зберігає їх у базу даних Room.
 */
@Composable
fun RadiationScreen(
    navController: NavController,
    operatorName: String = "Черговий оператор",
    stationName: String = "Моніторинг АЕС",
    viewModel: RadiationViewModel = viewModel()
) {
    // Читаємо показники прямо з ViewModel (стан зберігається у ViewModel та Room)
    val currentReading = viewModel.currentReading

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Рядок із інформацією про оператора
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Default.AccountCircle, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Оператор: $operatorName",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }

        // Кнопка повернення на головну
        Button(
            onClick = { navController.navigate(Screen.MainScreen.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Перейти на головну", fontSize = 16.sp)
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        // Заголовок станції (передане значення)
        Text(
            text = stationName,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Картка поточного стану радіації
        StatusCard(reading = currentReading)

        Spacer(modifier = Modifier.height(12.dp))

        // Кнопка оновлення показників датчика
        Button(
            onClick = {
                viewModel.generateNextReading()
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Icon(Icons.Default.Warning, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Оновити показники датчика")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка переходу до екрана історії
        Button(
            onClick = { navController.navigate(Screen.HistoryScreen.route) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Переглянути історію", fontSize = 16.sp)
        }
    }
}