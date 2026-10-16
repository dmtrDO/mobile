package com.example.npp_radiation_monitor.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) інтерфейс для виконання SQL-запитів у базі даних Room.
 */
@Dao
interface RadiationDao {

    /**
     * Зберігає нове вимірювання радіації у базі даних.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: RadiationEntity)

    /**
     * Отримує всі збережені вимірювання з базі даних у вигляді реактивного Flow списку.
     */
    @Query("SELECT * FROM radiation_readings ORDER BY id DESC")
    fun getAllReadings(): Flow<List<RadiationEntity>>
}
