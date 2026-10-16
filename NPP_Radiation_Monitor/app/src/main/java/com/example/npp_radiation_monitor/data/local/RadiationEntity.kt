package com.example.npp_radiation_monitor.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Сутність Room Database (Entity), що описує таблицю збереження замірів радіації.
 */
@Entity(tableName = "radiation_readings")
data class RadiationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val value: Float,
    val timestamp: String,
    val statusTitle: String
)
