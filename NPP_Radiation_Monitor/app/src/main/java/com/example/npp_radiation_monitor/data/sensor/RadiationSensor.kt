package com.example.npp_radiation_monitor.data.sensor

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.npp_radiation_monitor.data.model.RadiationReading
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

interface RadiationSensor {
    fun getReading(id: Int): RadiationReading
}

class SimulatedRadiationSensor : RadiationSensor {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun getReading(id: Int): RadiationReading {
        val value = Random.nextFloat() * 1.0f // Значення від 0.00 до 1.00 мкЗв/год
        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        return RadiationReading(
            id = id,
            value = value,
            timestamp = time
        )
    }
}