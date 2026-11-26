package com.example.npp_radiation_monitor.data.flow

import com.example.npp_radiation_monitor.data.model.RadiationReading
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt
import kotlin.random.Random

/**
 * Джерело асинхронних потоків даних (Flow) для моніторингу радіації.
 */
class RadiationFlowSource {

    /**
     * Створює "холодний" потік Flow, який кожні 1500 мс імітує отримання даних від датчика радіації.
     */
    fun getRadiationSensorStream(): Flow<RadiationReading> {
        var count = 1
        return flow {
            while (true) {
                delay(1500)

                if (count % 10 == 0) {
                    throw IllegalStateException("Помилка зчитування датчика радіації (Сенсор не відповідає)")
                }

                val rawValue = Random.nextFloat() * 0.95f
                val time = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())

                emit(
                    RadiationReading(
                        id = count++,
                        value = rawValue,
                        timestamp = time
                    )
                )
            }
        }
        .filter { reading ->
            reading.value >= 0.05f
        }
        .map { reading ->
            val roundedValue = (reading.value * 100).roundToInt() / 100.0f
            reading.copy(value = roundedValue)
        }
        .catch {
            val errorTime = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
            emit(
                RadiationReading(
                    id = -1,
                    value = 0.0f,
                    timestamp = errorTime
                )
            )
        }
    }
}
