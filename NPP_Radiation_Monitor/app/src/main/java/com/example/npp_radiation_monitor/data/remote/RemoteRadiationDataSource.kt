package com.example.npp_radiation_monitor.data.remote

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.npp_radiation_monitor.data.model.RadiationReading
import kotlinx.coroutines.delay
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Віддалене джерело даних (Remote Data Source).
 * Симулює взаємодію з віддаленим REST API сервером або мережевою станцією АЕС через корутини.
 * Включає симуляцію затримки мережі та можливість виклику мережевих помилок для перевірки обробки помилок.
 */
class RemoteRadiationDataSource {

    private val remoteStorage = mutableListOf<RadiationReading>()
    private var isSimulateError = false
    private var nextRemoteId = 100

    init {
        // Початкові віддалені дані з центрального сервера АЕС
        remoteStorage.addAll(
            listOf(
                RadiationReading(id = 101, value = 0.14f, timestamp = "10:00:00"),
                RadiationReading(id = 102, value = 0.28f, timestamp = "10:15:00"),
                RadiationReading(id = 103, value = 0.45f, timestamp = "10:30:00"),
                RadiationReading(id = 104, value = 0.72f, timestamp = "10:45:00")
            )
        )
    }

    /**
     * Отримує список замірів з віддаленого сервера із симуляцією затримки мережі.
     */
    suspend fun fetchRemoteReadings(): Result<List<RadiationReading>> {
        delay(800) // Симуляція мережевого запиту (Network latency)

        return if (isSimulateError) {
            Result.failure(Exception("Помилка підключення до центрального сервера АЕС (HTTP 503)"))
        } else {
            Result.success(remoteStorage.toList())
        }
    }

    /**
     * Надсилає новий замір на віддалений сервер.
     */
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun sendReadingToServer(value: Float): Result<RadiationReading> {
        delay(600)

        if (isSimulateError) {
            return Result.failure(Exception("Не вдалося відправити дані на сервер: відсутній зв'язок"))
        }

        val time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))
        val newReading = RadiationReading(
            id = ++nextRemoteId,
            value = value,
            timestamp = time
        )
        remoteStorage.add(0, newReading)
        return Result.success(newReading)
    }

    /**
     * Перемикач режиму симуляції помилки мережі для тестування error-handling в Repository.
     */
    fun toggleErrorSimulation(enable: Boolean) {
        isSimulateError = enable
    }

    fun isErrorSimulated(): Boolean = isSimulateError
}
