package com.example.npp_radiation_monitor.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.npp_radiation_monitor.data.model.RadiationReading
import java.util.Locale

/**
 * Компонент картки статусу радіаційного фону (StatusCard).
 * Відображає поточний рівень радіації із анімованим зміненням кольору
 * та значення залежно від статусу безпеки.
 */
@Composable
fun StatusCard(
    reading: RadiationReading,
    modifier: Modifier = Modifier
) {
    // Анімації значення радіації та кольору картки
    val animatedRadiation by animateFloatAsState(targetValue = reading.value)
    val animatedColor by animateColorAsState(targetValue = reading.status.color)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp),
        colors = CardDefaults.cardColors(containerColor = animatedColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Текст статусу (Норма, Увага, Небезпека)
            Text(
                text = "Статус: ${reading.status.title}",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium
            )
            // Анімоване числове значення радіації
            Text(
                text = String.format(locale = Locale.UK, "%.2f", animatedRadiation),
                color = Color.White,
                fontSize = 56.sp,
                fontWeight = FontWeight.Black
            )
            // Одиниці вимірювання
            Text(
                text = "мкЗв/год",
                color = Color.White,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}