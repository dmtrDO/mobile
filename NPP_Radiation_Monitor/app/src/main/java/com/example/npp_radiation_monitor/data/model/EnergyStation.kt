package com.example.npp_radiation_monitor.data.model

/**
 * Модель даних енергетичної станції / об'єкта моніторингу АЕС з координатами GPS.
 */
data class EnergyStation(
    val id: Int,
    val name: String,
    val stationType: String,
    val status: String,
    val radiationValue: Float,
    val location: String,
    val latitude: Double = 50.13,
    val longitude: Double = 26.65
)
