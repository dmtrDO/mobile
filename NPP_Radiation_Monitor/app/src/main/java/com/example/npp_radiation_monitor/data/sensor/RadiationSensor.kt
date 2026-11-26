package com.example.npp_radiation_monitor.data.sensor

import com.example.npp_radiation_monitor.data.model.RadiationReading
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random

interface RadiationSensor {
    fun getReading(id: Int): RadiationReading
}

class SimulatedRadiationSensor : RadiationSensor {
    override fun getReading(id: Int): RadiationReading {
        val value = Random.nextFloat() * 1.0f
        val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        return RadiationReading(
            id = id,
            value = value,
            timestamp = time
        )
    }
}
