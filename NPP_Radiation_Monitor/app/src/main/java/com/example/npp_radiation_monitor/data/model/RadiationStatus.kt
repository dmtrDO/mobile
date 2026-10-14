package com.example.npp_radiation_monitor.data.model

import androidx.compose.ui.graphics.Color

enum class RadiationStatus(
    val title: String,
    val color: Color,
    val maxThreshold: Float
) {
    SAFE("Безпечно", Color(0xFF4CAF50), 0.30f),
    WARNING("Увага", Color(0xFFFFC107), 0.60f),
    DANGER("Небезпека", Color(0xFFF44336), Float.MAX_VALUE);

    companion object {
        fun fromValue(value: Float): RadiationStatus {
            return when {
                value < SAFE.maxThreshold -> SAFE
                value < WARNING.maxThreshold -> WARNING
                else -> DANGER
            }
        }
    }
}