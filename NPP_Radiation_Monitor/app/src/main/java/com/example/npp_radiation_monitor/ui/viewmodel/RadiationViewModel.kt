package com.example.npp_radiation_monitor.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.npp_radiation_monitor.data.RadiationManager
import com.example.npp_radiation_monitor.data.local.AppDatabase
import com.example.npp_radiation_monitor.data.local.RadiationEntity
import com.example.npp_radiation_monitor.data.model.RadiationReading
import com.example.npp_radiation_monitor.data.repository.RadiationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * ViewModel для управління станом показників радіаційного фону та локальною базою даних Room (RadiationViewModel).
 * Інтегровано з RadiationRepository для збереження замірів у локальне сховище Room Database.
 */
class RadiationViewModel(application: Application) : AndroidViewModel(application) {

    private val radiationManager = RadiationManager()
    private val repository: RadiationRepository

    // Поточний об'єкт вимірювання радіації
    var currentReading by mutableStateOf(radiationManager.createInitialReading())
        private set

    // Реактивний потік усіх записів із бази даних Room
    val allReadingsFromRoom: Flow<List<RadiationEntity>>

    init {
        val dao = AppDatabase.getDatabase(application).radiationDao()
        repository = RadiationRepository(dao)
        allReadingsFromRoom = repository.allReadings
    }

    /**
     * Генерує нове значення радіаційного фону та зберігає його у локальну базу даних Room.
     */
    fun generateNextReading(): RadiationReading {
        val newReading = radiationManager.generateNextReading()
        currentReading = newReading

        // Зберігаємо замір у локальну базу Room (асинхронно через Coroutine)
        viewModelScope.launch {
            repository.insertReading(
                RadiationEntity(
                    value = newReading.value,
                    timestamp = newReading.timestamp,
                    statusTitle = newReading.status.title
                )
            )
        }

        return newReading
    }
}
