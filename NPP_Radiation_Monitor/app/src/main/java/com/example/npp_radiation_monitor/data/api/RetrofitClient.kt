package com.example.npp_radiation_monitor.data.api

import com.example.npp_radiation_monitor.data.model.api.RadiationForecastResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton клієнт Retrofit з Gson-конвертером для виконання HTTP REST API запитів.
 */
object RetrofitClient {

    private const val BASE_URL = "https://api.npp-radiation.example.com/"

    // Створення екземпляра Gson для серіалізації/десеріалізації JSON
    val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    // Створення клієнта Retrofit з налаштованим GsonConverterFactory
    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * Реалізація API сервісу через Retrofit.
     */
    val apiService: RadiationApiService by lazy {
        retrofit.create(RadiationApiService::class.java)
    }

    /**
     * Резервний метод із симуляцією отримання JSON та парсингу через Gson
     * для гарантованої демонстрації роботи базового рівню без зовнішньої залежності від реального сервера.
     */
    fun parseMockJsonResponse(): RadiationForecastResponse {
        val mockJson = """
            {
                "station_name": "Хмельницька АЕС",
                "station_id": "NPP-KHN-01",
                "status": "Штатний режим",
                "forecast_power_mw": 1000.0,
                "radiation_level_usv": 0.12,
                "last_updated": "12:00:00"
            }
        """.trimIndent()

        // Десеріалізація JSON-рядка у Kotlin-об'єкт за допомогою Gson
        return gson.fromJson(mockJson, RadiationForecastResponse::class.java)
    }
}
