package com.example.npp_radiation_monitor.data.repository

import com.example.npp_radiation_monitor.data.local.RadiationDao
import com.example.npp_radiation_monitor.data.local.RadiationEntity
import kotlinx.coroutines.flow.Flow

/**
 * Репозиторій для роботи з даними радіаційного фону (RadiationRepository).
 * Виступає посередником між джерелом даних (Room DAO) та ViewModel.
 */
class RadiationRepository(private val radiationDao: RadiationDao) {

    /**
     * Потік усіх збережених у базі вимірювань.
     */
    val allReadings: Flow<List<RadiationEntity>> = radiationDao.getAllReadings()

    /**
     * Додає нове вимірювання у базу даних Room.
     */
    suspend fun insertReading(reading: RadiationEntity) {
        radiationDao.insertReading(reading)
    }
}
