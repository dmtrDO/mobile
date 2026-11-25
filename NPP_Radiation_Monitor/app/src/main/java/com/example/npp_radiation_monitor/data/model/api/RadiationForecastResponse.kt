package com.example.npp_radiation_monitor.data.model.api

import com.google.gson.annotations.SerializedName

/**
 * DTO (Data Transfer Object) для десеріалізації JSON-відповіді REST API за допомогою бібліотеки Gson.
 */
data class RadiationForecastResponse(
    @SerializedName("station_name")
    val stationName: String,

    @SerializedName("station_id")
    val stationId: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("forecast_power_mw")
    val forecastPowerMw: Float,

    @SerializedName("radiation_level_usv")
    val radiationLevelUsv: Float,

    @SerializedName("last_updated")
    val lastUpdated: String
)
