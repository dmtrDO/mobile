package com.example.npp_radiation_monitor.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * DAO (Data Access Object) інтерфейс для виконання SQL-запитів у базі даних Room.
 * Підтримує повний набір CRUD-операцій (Create, Read, Update, Delete) та пошук.
 */
@Dao
interface RadiationDao {

    /**
     * Створення: Додає нове вимірювання радіації у базу даних.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: RadiationEntity): Long

    /**
     * Пакетне додавання віддалених/кешованих даних.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(readings: List<RadiationEntity>)

    /**
     * Читання: Отримує всі збережені вимірювання з базі даних у вигляді реактивного Flow списку.
     */
    @Query("SELECT * FROM radiation_readings ORDER BY id DESC")
    fun getAllReadings(): Flow<List<RadiationEntity>>

    /**
     * Оновлення: Редагування існуючого заміру за ідентифікатором.
     */
    @Update
    suspend fun updateReading(reading: RadiationEntity)

    /**
     * Видалення: Видаляє конкретну сутність вимірювання з бази даних.
     */
    @Delete
    suspend fun deleteReading(reading: RadiationEntity)

    /**
     * Видалення за ID.
     */
    @Query("DELETE FROM radiation_readings WHERE id = :id")
    suspend fun deleteReadingById(id: Int)

    /**
     * Повний очист історії вимірювань у локальній БД.
     */
    @Query("DELETE FROM radiation_readings")
    suspend fun clearAll()

    /**
     * Пошук замірів за часом або статусом.
     */
    @Query("SELECT * FROM radiation_readings WHERE timestamp LIKE '%' || :query || '%' OR statusTitle LIKE '%' || :query || '%' ORDER BY id DESC")
    fun searchReadings(query: String): Flow<List<RadiationEntity>>
}
