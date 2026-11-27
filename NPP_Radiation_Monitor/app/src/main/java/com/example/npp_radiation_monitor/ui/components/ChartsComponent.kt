package com.example.npp_radiation_monitor.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.npp_radiation_monitor.data.model.EnergyStation
import com.example.npp_radiation_monitor.data.model.RadiationReading
import java.util.Locale
import kotlin.math.abs

/**
 * Інтерактивний компонент для побудови лінійних та стовпчикових графіків візуалізації даних.
 */
@Composable
fun ChartsComponent(
    readings: List<RadiationReading>,
    stations: List<EnergyStation>
) {
    var selectedChartType by remember { mutableIntStateOf(0) } // 0 = LineChart, 1 = BarChart
    var selectedReadingIndex by remember { mutableIntStateOf(-1) }
    var selectedStationIndex by remember { mutableIntStateOf(-1) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                text = "Графіки та діаграми енергоспоживання:",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Вибір типу графіка (перемикач між лінійним та стовпчиковим графіком)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedChartType == 0,
                    onClick = {
                        selectedChartType = 0
                        selectedReadingIndex = -1
                    },
                    label = { Text("Лінійний графік (динаміка)", fontSize = 11.sp) }
                )
                FilterChip(
                    selected = selectedChartType == 1,
                    onClick = {
                        selectedChartType = 1
                        selectedStationIndex = -1
                    },
                    label = { Text("Стовпчиковий графік (порівняння)", fontSize = 11.sp) }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (selectedChartType == 0) {
                // 1. ЛІНІЙНИЙ ГРАФІК (динаміка показників у часі)
                val chartData = if (readings.isEmpty()) {
                    listOf(
                        RadiationReading(1, 0.12f, "10:00:00"),
                        RadiationReading(2, 0.28f, "10:15:00"),
                        RadiationReading(3, 0.18f, "10:30:00"),
                        RadiationReading(4, 0.45f, "10:45:00"),
                        RadiationReading(5, 0.22f, "11:00:00")
                    )
                } else readings.take(8)

                val maxValue = (chartData.maxOfOrNull { it.value } ?: 1.0f).coerceAtLeast(0.5f)

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .pointerInput(chartData) {
                            detectTapGestures { offset ->
                                val width = size.width.toFloat()
                                val stepX = width / (chartData.size.coerceAtLeast(2) - 1)

                                chartData.forEachIndexed { index, _ ->
                                    val pointX = index * stepX
                                    if (abs(offset.x - pointX) < stepX / 2f) {
                                        selectedReadingIndex = index
                                    }
                                }
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val padding = 20f

                    // Малювання сітки графіка
                    for (i in 1..3) {
                        val y = height * (i / 4f)
                        drawLine(
                            color = Color.LightGray.copy(alpha = 0.5f),
                            start = Offset(0f, y),
                            end = Offset(width, y),
                            strokeWidth = 2f
                        )
                    }

                    val points = mutableListOf<Offset>()
                    val stepX = width / (chartData.size.coerceAtLeast(2) - 1)

                    chartData.forEachIndexed { index, reading ->
                        val x = index * stepX
                        val normalizedY = (reading.value / maxValue)
                        val y = height - padding - (normalizedY * (height - 2 * padding))
                        points.add(Offset(x, y))
                    }

                    // Малювання лінії графіка
                    if (points.size >= 2) {
                        val path = Path().apply {
                            moveTo(points[0].x, points[0].y)
                            for (i in 1 until points.size) {
                                lineTo(points[i].x, points[i].y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = Color(0xFF1976D2),
                            style = Stroke(width = 6f)
                        )
                    }

                    // Малювання точок графіка
                    points.forEachIndexed { index, point ->
                        val isSelected = index == selectedReadingIndex
                        drawCircle(
                            color = if (isSelected) Color(0xFFD32F2F) else Color(0xFF1976D2),
                            radius = if (isSelected) 14f else 8f,
                            center = point
                        )
                        drawCircle(
                            color = Color.White,
                            radius = if (isSelected) 6f else 4f,
                            center = point
                        )
                    }
                }

                // Інтерактивна підказка (tooltip) для обраної точки лінійного графіка
                if (selectedReadingIndex in chartData.indices) {
                    val activeReading = chartData[selectedReadingIndex]
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(
                            text = String.format(Locale.UK, "Обрана точка: час %s | Значення: %.2f мкЗв/год | Статус: %s", activeReading.timestamp, activeReading.value, activeReading.status.title),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

            } else {
                // 2. СТОВПЧИКОВИЙ ГРАФІК (порівняння показників між об'єктами АЕС)
                val barData = if (stations.isEmpty()) {
                    listOf(
                        EnergyStation(1, "Хмельницька АЕС", "АЕС", "Штатний", 0.12f, "м. Нетішин"),
                        EnergyStation(2, "Рівненська АЕС", "Пост", "Норма", 0.15f, "м. Вараш"),
                        EnergyStation(3, "Запорізький КП", "КП", "Увага", 0.28f, "м. Енергодар"),
                        EnergyStation(4, "Южно-Українська АЕС", "АЕС", "Штатний", 0.11f, "м. Южноукраїнськ")
                    )
                } else stations

                val maxBarValue = (barData.maxOfOrNull { it.radiationValue } ?: 1.0f).coerceAtLeast(0.5f)

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .pointerInput(barData) {
                            detectTapGestures { offset ->
                                val width = size.width.toFloat()
                                val barWidth = width / barData.size

                                barData.forEachIndexed { index, _ ->
                                    val startX = index * barWidth
                                    if (offset.x >= startX && offset.x <= startX + barWidth) {
                                        selectedStationIndex = index
                                    }
                                }
                            }
                        }
                ) {
                    val width = size.width
                    val height = size.height
                    val barWidth = (width / barData.size) * 0.65f
                    val spacing = (width / barData.size) * 0.35f

                    barData.forEachIndexed { index, station ->
                        val isSelected = index == selectedStationIndex
                        val barHeight = (station.radiationValue / maxBarValue) * (height - 30f)
                        val x = index * (barWidth + spacing) + spacing / 2f
                        val y = height - barHeight

                        drawRoundRect(
                            color = if (isSelected) Color(0xFFD32F2F) else Color(0xFF388E3C),
                            topLeft = Offset(x, y),
                            size = Size(barWidth, barHeight),
                            cornerRadius = CornerRadius(8f, 8f)
                        )
                    }
                }

                // Інтерактивна підказка (tooltip) для обраного стовпчика
                if (selectedStationIndex in barData.indices) {
                    val activeStation = barData[selectedStationIndex]
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Text(
                            text = String.format(Locale.UK, "Обраний об'єкт: %s | тип: %s | значення: %.2f мкЗв/год | стан: %s", activeStation.name, activeStation.stationType, activeStation.radiationValue, activeStation.status),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}
