package com.example.npp_radiation_monitor.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale

/**
 * Інтерактивний анімований дашборд візуалізації параметрів електростанції (потужність, температура, ефективність).
 */
@Composable
fun AnimatedDashboardComponent() {
    var powerValue by remember { mutableFloatStateOf(50f) }
    var tempValue by remember { mutableFloatStateOf(25f) }
    var efficiencyValue by remember { mutableFloatStateOf(52f) }
    var forecastMessage by remember { mutableStateOf<String?>(null) }

    // Анімація прогрес-барів за допомогою animateFloatAsState
    val animatedPower by animateFloatAsState(
        targetValue = powerValue / 100f,
        animationSpec = tween(durationMillis = 800),
        label = "PowerAnim"
    )
    val animatedTemp by animateFloatAsState(
        targetValue = tempValue / 60f,
        animationSpec = tween(durationMillis = 800),
        label = "TempAnim"
    )
    val animatedEfficiency by animateFloatAsState(
        targetValue = efficiencyValue / 100f,
        animationSpec = tween(durationMillis = 800),
        label = "EffAnim"
    )

    // Кольорова анімація залежно від значень
    val powerColor by animateColorAsState(
        targetValue = if (powerValue >= 80f) Color(0xFF4CAF50) else Color(0xFFFFEB3B),
        label = "PowerColor"
    )
    val tempColor by animateColorAsState(
        targetValue = if (tempValue >= 40f) Color(0xFFFFEB3B) else Color(0xFFF44336),
        label = "TempColor"
    )
    val efficiencyColor by animateColorAsState(
        targetValue = if (efficiencyValue >= 80f) Color(0xFF4CAF50) else Color(0xFFFFEB3B),
        label = "EffColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Інтерактивний дашборд електростанції:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Бар потужності
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        powerValue = if (powerValue == 50f) 90f else 50f
                        tempValue = if (tempValue == 25f) 45f else 25f
                        efficiencyValue = if (efficiencyValue == 52f) 100f else 52f
                        forecastMessage = String.format(Locale.UK, "Прогноз потужності: %.0f кВт | ефективність: %.0f%%", powerValue, efficiencyValue)
                    }
            ) {
                Text(
                    text = String.format(Locale.UK, "Потужність (кВт): %.0f", powerValue),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(22.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedPower)
                            .height(22.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(powerColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Бар температури
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        powerValue = if (powerValue == 50f) 90f else 50f
                        tempValue = if (tempValue == 25f) 45f else 25f
                        efficiencyValue = if (efficiencyValue == 52f) 100f else 52f
                        forecastMessage = String.format(Locale.UK, "Прогноз потужності: %.0f кВт | ефективність: %.0f%%", powerValue, efficiencyValue)
                    }
            ) {
                Text(
                    text = String.format(Locale.UK, "Температура (°C): %.0f", tempValue),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(22.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedTemp)
                            .height(22.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(tempColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Бар ефективності
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        powerValue = if (powerValue == 50f) 90f else 50f
                        tempValue = if (tempValue == 25f) 45f else 25f
                        efficiencyValue = if (efficiencyValue == 52f) 100f else 52f
                        forecastMessage = String.format(Locale.UK, "прогноз потужності: %.0f кВт | ефективність: %.0f%%", powerValue, efficiencyValue)
                    }
            ) {
                Text(
                    text = String.format(Locale.UK, "Ефективність (%%): %.0f", efficiencyValue),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(22.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedEfficiency)
                            .height(22.dp)
                            .clip(RoundedCornerShape(11.dp))
                            .background(efficiencyColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Натисніть на бар для отримання прогнозу потужності",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                fontSize = 11.sp
            )

            if (forecastMessage != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Text(
                        text = forecastMessage!!,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}
