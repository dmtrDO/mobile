package com.example.npp_radiation_monitor.data

import com.example.npp_radiation_monitor.data.model.RadiationReading
import com.example.npp_radiation_monitor.data.sensor.RadiationSensor
import com.example.npp_radiation_monitor.data.sensor.SimulatedRadiationSensor

class RadiationManager(
    private val sensor: RadiationSensor = SimulatedRadiationSensor()
) {
    private var nextId = 0

    fun createInitialReading(): RadiationReading {
        return sensor.getReading(nextId++)
    }

    fun generateNextReading(): RadiationReading {
        return sensor.getReading(nextId++)
    }
}