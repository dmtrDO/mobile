package com.example.npp_radiation_monitor.data.model

data class RadiationReading(
    val id: Int,
    val value: Float,
    val timestamp: String,
    val status: RadiationStatus = RadiationStatus.fromValue(value)
)