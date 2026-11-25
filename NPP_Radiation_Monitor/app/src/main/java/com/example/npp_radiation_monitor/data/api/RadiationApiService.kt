package com.example.npp_radiation_monitor.data.api

import com.example.npp_radiation_monitor.data.model.api.RadiationForecastResponse
import retrofit2.Response
import retrofit2.http.GET

/**
 * Інтерфейс Retrofit для опису REST API ендпоінтів взаємодії із сервером АЕС
 */
interface RadiationApiService {

    /**
     * Базовий REST-ендпоінт для отримання JSON-прогнозу та показників радіації.
     */
    @GET("v1/radiation-forecast")
    suspend fun getRadiationForecast(): Response<RadiationForecastResponse>
}
