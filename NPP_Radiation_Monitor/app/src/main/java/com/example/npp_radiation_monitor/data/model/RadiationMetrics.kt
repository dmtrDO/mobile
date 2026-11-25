package com.example.npp_radiation_monitor.data.model

/**
 * Модель даних для збереження агрегованої бізнес-аналітики вимірювань радіаційного фону.
 * Обчислюється на рівні RadiationRepository.
 */
data class RadiationMetrics(
    val averageValue: Float = 0.0f,
    val maxValue: Float = 0.0f,
    val totalCount: Int = 0,
    val dangerCount: Int = 0,
    val warningCount: Int = 0,
    val safeCount: Int = 0
)
