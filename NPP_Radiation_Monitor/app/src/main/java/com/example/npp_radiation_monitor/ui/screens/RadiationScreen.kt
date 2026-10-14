package com.example.npp_radiation_monitor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.npp_radiation_monitor.data.RadiationManager
import com.example.npp_radiation_monitor.data.model.RadiationReading
import com.example.npp_radiation_monitor.ui.components.AuthDialog
import com.example.npp_radiation_monitor.ui.components.HistoryItem
import com.example.npp_radiation_monitor.ui.components.StatusCard

@Composable
fun RadiationScreen(
    radiationManager: RadiationManager = remember { RadiationManager() }
) {
    var currentReading by remember { mutableStateOf(radiationManager.createInitialReading()) }
    val history = remember { mutableStateListOf<RadiationReading>() }

    // Стан для роботи з авторизацією
    var showAuthDialog by remember { mutableStateOf(false) }
    var operatorEmail by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- ВЕРХНЯ ШАПКА З АВТОРИЗАЦІЄЮ ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "Профіль",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (operatorEmail != null) "Оператор: $operatorEmail" else "Статус: Гість",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (operatorEmail == null) {
                OutlinedButton(onClick = { showAuthDialog = true }) {
                    Text("Увійти")
                }
            } else {
                TextButton(onClick = { operatorEmail = null }) {
                    Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Вийти")
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

        // --- ЗАГОЛОВОК СТАНЦІЇ ---
        Text(
            text = "Моніторинг АЕС",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // --- КАРТКА СТАНУ ---
        StatusCard(reading = currentReading)

        Spacer(modifier = Modifier.height(12.dp))

        // --- КНОПКА ОНОВЛЕННЯ ---
        Button(
            onClick = {
                val newReading = radiationManager.generateNextReading()
                currentReading = newReading
                history.add(0, newReading)
            },
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large
        ) {
            Icon(Icons.Default.Warning, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Оновити показники датчика")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- ІСТОРІЯ ---
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

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(history) { reading ->
                HistoryItem(reading = reading)
            }
        }
    }

    // --- ПОКАЗ СПЛИВАЮЧОГО ВІКНА АВТОРИЗАЦІЇ ---
    if (showAuthDialog) {
        AuthDialog(
            onDismissRequest = { showAuthDialog = false },
            onSuccess = { email ->
                operatorEmail = email
                showAuthDialog = false
            }
        )
    }
}