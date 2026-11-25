package com.example.npp_radiation_monitor.data.repository

import com.example.npp_radiation_monitor.data.api.RetrofitClient
import com.example.npp_radiation_monitor.data.local.RadiationDao
import com.example.npp_radiation_monitor.data.local.RadiationEntity
import com.example.npp_radiation_monitor.data.model.RadiationMetrics
import com.example.npp_radiation_monitor.data.model.RadiationReading
import com.example.npp_radiation_monitor.data.model.RadiationStatus
import com.example.npp_radiation_monitor.data.model.api.RadiationForecastResponse
import com.example.npp_radiation_monitor.data.remote.RemoteRadiationDataSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Перерахунок доступних режимів роботи з джерелами даних (DataSourceMode).
 * Агрегує різноманітні стратегії отримання інформації.
 */
enum class DataSourceMode(val title: String) {
    LOCAL_ONLY("Локальна БД (Room)"),
    REMOTE_ONLY("Віддалене API (Сервер)"),
    AGGREGATED_CACHE("Агрегований режим + Кешування")
}

/**
 * Режим сортування замірів радіації.
 */
enum class SortOrder(val title: String) {
    BY_TIME_DESC("За датою (нові спочатку)"),
    BY_TIME_ASC("За датою (старі спочатку)"),
    BY_VALUE_DESC("За спаданням рівню"),
    BY_VALUE_ASC("За зростанням рівню")
}

/**
 * Комплексний репозиторій для моніторингу радіації (RadiationRepository).
 */
class RadiationRepository(
    private val localDao: RadiationDao,
    private val remoteDataSource: RemoteRadiationDataSource = RemoteRadiationDataSource(),
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    /**
     * Потік локальних замірів із Room у вигляді доменних моделей RadiationReading.
     */
    val localReadings: Flow<List<RadiationReading>> = localDao.getAllReadings().map { list ->
        list.map { entity ->
            RadiationReading(
                id = entity.id,
                value = entity.value,
                timestamp = entity.timestamp,
                status = RadiationStatus.fromValue(entity.value)
            )
        }
    }

    /**
     * Синхронізація віддалених даних із локальним кешем Room (Стратегія Offline-First Cache).
     */
    suspend fun syncRemoteWithLocalCache(): Result<List<RadiationReading>> = withContext(ioDispatcher) {
        val remoteResult = remoteDataSource.fetchRemoteReadings()

        if (remoteResult.isSuccess) {
            val remoteReadings = remoteResult.getOrDefault(emptyList())
            
            // Кешування у базі Room
            val entities = remoteReadings.map { reading ->
                RadiationEntity(
                    id = if (reading.id > 0) reading.id else 0,
                    value = reading.value,
                    timestamp = reading.timestamp,
                    statusTitle = reading.status.title
                )
            }
            localDao.insertAll(entities)
            Result.success(remoteReadings)
        } else {
            // У разі помилки мережі повертаємо результат з помилкою для відображення в UI
            Result.failure(remoteResult.exceptionOrNull() ?: Exception("Невідома помилка мережі"))
        }
    }

    /**
     * Отримання віддалених даних напряму з сервера.
     */
    suspend fun getRemoteReadings(): Result<List<RadiationReading>> = withContext(ioDispatcher) {
        remoteDataSource.fetchRemoteReadings()
    }

    /**
     * Додає нове вимірювання радіації локально та на віддалений сервер (якщо є зв'язок).
     */
    suspend fun addReading(value: Float, timestamp: String, saveToRemote: Boolean = false): Result<Unit> = withContext(ioDispatcher) {
        try {
            val status = RadiationStatus.fromValue(value)
            val entity = RadiationEntity(
                value = value,
                timestamp = timestamp,
                statusTitle = status.title
            )
            localDao.insertReading(entity)

            if (saveToRemote) {
                remoteDataSource.sendReadingToServer(value)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Оновлює існуюче вимірювання радіації у локальній БД (CRUD - Update).
     */
    suspend fun updateReading(id: Int, value: Float, timestamp: String): Result<Unit> = withContext(ioDispatcher) {
        try {
            val status = RadiationStatus.fromValue(value)
            localDao.updateReading(
                RadiationEntity(
                    id = id,
                    value = value,
                    timestamp = timestamp,
                    statusTitle = status.title
                )
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Видаляє вимірювання за ID (CRUD - Delete).
     */
    suspend fun deleteReading(id: Int): Result<Unit> = withContext(ioDispatcher) {
        try {
            localDao.deleteReadingById(id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Очищає всю історію замірів.
     */
    suspend fun clearAllReadings(): Result<Unit> = withContext(ioDispatcher) {
        try {
            localDao.clearAll()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Обчислює агреговані статистичні метрики для списку замірів (Середній рівень, Макс значення, підрахунок тривог).
     */
    fun calculateMetrics(readings: List<RadiationReading>): RadiationMetrics {
        if (readings.isEmpty()) return RadiationMetrics()

        val avg = readings.map { it.value }.average().toFloat()
        val max = readings.maxOfOrNull { it.value } ?: 0.0f
        val safeCount = readings.count { it.status == RadiationStatus.SAFE }
        val warningCount = readings.count { it.status == RadiationStatus.WARNING }
        val dangerCount = readings.count { it.status == RadiationStatus.DANGER }

        return RadiationMetrics(
            averageValue = avg,
            maxValue = max,
            totalCount = readings.size,
            safeCount = safeCount,
            warningCount = warningCount,
            dangerCount = dangerCount
        )
    }

    /**
     * Фільтрація та сортування вимірювань за параметрами.
     */
    fun filterAndSortReadings(
        readings: List<RadiationReading>,
        searchQuery: String,
        statusFilter: RadiationStatus?,
        sortOrder: SortOrder
    ): List<RadiationReading> {
        var filtered = readings

        // 1. Пошук за часом або ID
        if (searchQuery.isNotBlank()) {
            filtered = filtered.filter {
                it.timestamp.contains(searchQuery, ignoreCase = true) ||
                        it.id.toString().contains(searchQuery) ||
                        it.status.title.contains(searchQuery, ignoreCase = true)
            }
        }

        // 2. Фільтрація за статусом безпеки
        if (statusFilter != null) {
            filtered = filtered.filter { it.status == statusFilter }
        }

        // 3. Сортування
        return when (sortOrder) {
            SortOrder.BY_TIME_DESC -> filtered.sortedByDescending { it.id }
            SortOrder.BY_TIME_ASC -> filtered.sortedBy { it.id }
            SortOrder.BY_VALUE_DESC -> filtered.sortedByDescending { it.value }
            SortOrder.BY_VALUE_ASC -> filtered.sortedBy { it.value }
        }
    }

    /**
     * Перемикання режиму симуляції помилки для тестування.
     */
    fun toggleRemoteErrorSimulation(enable: Boolean) {
        remoteDataSource.toggleErrorSimulation(enable)
    }

    fun isRemoteErrorSimulated(): Boolean = remoteDataSource.isErrorSimulated()

    /**
     Виклик REST API за допомогою Retrofit та парсинг JSON з Gson.
     */
    suspend fun fetchRestApiForecast(): Result<RadiationForecastResponse> = withContext(ioDispatcher) {
        try {
            val response = RetrofitClient.apiService.getRadiationForecast()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                val fallbackData = RetrofitClient.parseMockJsonResponse()
                Result.success(fallbackData)
            }
        } catch (e: Exception) {
            val fallbackData = RetrofitClient.parseMockJsonResponse()
            Result.success(fallbackData)
        }
    }
}
