package com.example.npp_radiation_monitor.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale

/**
 * Кастомний UI-компонент візуалізації векторних даних сенсорів (акселерометра та гіроскопа) на Canvas.
 */
@Composable
fun SensorVectorComponent(
    accelX: Float,
    accelY: Float,
    gyroZ: Float
) {
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
                text = String.format(Locale.UK, "Accelerometer X: %.2f, Y: %.2f", accelX, accelY),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = String.format(Locale.UK, "Gyroscope Z: %.2f", gyroZ),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Візуальне малювання векторних ліній на Canvas (акселерометр X та Y)
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val scale = 8f

                // Лінія вектора X (синя)
                drawLine(
                    color = Color.Blue,
                    start = center,
                    end = Offset(center.x + (accelX * scale), center.y),
                    strokeWidth = 6f
                )

                // Лінія вектора Y (червона)
                drawLine(
                    color = Color.Red,
                    start = center,
                    end = Offset(center.x, center.y + (accelY * scale)),
                    strokeWidth = 6f
                )

                // Центральна точка координатної сітки
                drawCircle(
                    color = Color.DarkGray,
                    radius = 4f,
                    center = center
                )
            }
        }
    }
}
