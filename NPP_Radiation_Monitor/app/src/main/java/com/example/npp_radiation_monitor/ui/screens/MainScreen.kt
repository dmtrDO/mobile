package com.example.npp_radiation_monitor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.npp_radiation_monitor.navigation.Screen

/**
 * Головний екран застосунку (MainScreen).
 * Містить форму введення імені оператора та кнопку для переходу
 * до моніторингу радіації з передачею даних через аргументи навігації.
 */
@Composable
fun MainScreen(navController: NavController) {
    // Стан для збереження введеного імені оператора
    var operatorName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Заголовок екрана
        Text(
            text = "Головний екран",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.padding(16.dp))

        // Форма введення імені оператора
        OutlinedTextField(
            value = operatorName,
            onValueChange = { operatorName = it },
            label = { Text("Введіть ім'я оператора") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка переходу до екрана моніторингу
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.navigate(
                    Screen.RadiationScreen.createRoute(operatorName = operatorName)
                )
            }
        ) {
            Text(text = "Моніторинг радіації", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Кнопка переходу до повної форми реєстрації/налаштування станції (Заняття №6)
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                navController.navigate(Screen.FormScreen.route)
            }
        ) {
            Text(text = "Форма реєстрації станції", fontSize = 16.sp)
        }
    }
}

