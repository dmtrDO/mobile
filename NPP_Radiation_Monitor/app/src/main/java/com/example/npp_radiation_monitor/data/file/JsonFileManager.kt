package com.example.npp_radiation_monitor.data.file

import android.content.Context
import com.example.npp_radiation_monitor.data.model.RadiationReading
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Клас для асинхронного збереження та читання структурованих даних у форматі JSON.
 */
class JsonFileManager(private val context: Context) {

    private val gson = Gson()
    private val fileName = "radiation_analytics.json"

    /**
     * Запис списку замірів у локальний JSON-файл за допомогою бібліотеки Gson.
     */
    suspend fun saveToJson(readings: List<RadiationReading>): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val jsonString = gson.toJson(readings)
                val file = File(context.filesDir, fileName)
                file.writeText(jsonString)
                Result.success("" +
                        "Дані успішно збережено у JSON (${readings.size} записів)")
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Читання та десеріалізація об'єктів з локального JSON-файлу.
     */
    suspend fun readFromJson(): Result<List<RadiationReading>> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(context.filesDir, fileName)
                if (!file.exists()) {
                    return@withContext Result.success(emptyList())
                }
                val jsonString = file.readText()
                val listType = object : TypeToken<List<RadiationReading>>() {}.type
                val readings: List<RadiationReading> = gson.fromJson(jsonString, listType) ?: emptyList()
                Result.success(readings)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * Очищення локального JSON-файлу.
     */
    suspend fun clearJsonFile(): Boolean {
        return withContext(Dispatchers.IO) {
            val file = File(context.filesDir, fileName)
            if (file.exists()) file.delete() else true
        }
    }
}
