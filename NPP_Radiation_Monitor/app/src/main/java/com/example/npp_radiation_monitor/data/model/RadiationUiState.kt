package com.example.npp_radiation_monitor.data.model

import com.example.npp_radiation_monitor.data.model.api.RadiationForecastResponse
import com.example.npp_radiation_monitor.data.model.auth.AuthState
import com.example.npp_radiation_monitor.data.repository.DataSourceMode
import com.example.npp_radiation_monitor.data.repository.SortOrder

/**
 * Стан користувацького інтерфейсу (UI State) для екрана моніторингу та ресурсів.
 */
data class RadiationUiState(
    val filteredReadings: List<RadiationReading> = emptyList(),
    val rawReadings: List<RadiationReading> = emptyList(),
    val metrics: RadiationMetrics = RadiationMetrics(),
    val currentMode: DataSourceMode = DataSourceMode.LOCAL_ONLY,
    val searchQuery: String = "",
    val statusFilter: RadiationStatus? = null,
    val sortOrder: SortOrder = SortOrder.BY_TIME_DESC,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isRemoteErrorSimulated: Boolean = false,
    val restApiData: RadiationForecastResponse? = null,
    val authState: AuthState = AuthState(),
    val isNetworkConnected: Boolean = true,
    val isSimulatedOffline: Boolean = false,
    val networkStatusMessage: String? = null,
    val isFlowActive: Boolean = false,
    val realtimeReading: RadiationReading? = null,
    val flowEmittedCount: Int = 0,
    val energyStations: List<EnergyStation> = emptyList(),
    val selectedDetailReading: RadiationReading? = null,
    val userLatitude: Double = 50.4501,
    val userLongitude: Double = 30.5234,
    val selectedMapStation: EnergyStation? = null,
    val accelerometerX: Float = 0.15f,
    val accelerometerY: Float = -0.32f,
    val accelerometerZ: Float = 9.78f,
    val accelerometerMagnitude: Float = 9.81f,
    val gyroscopeZ: Float = 0.0f,
    val isSensorActive: Boolean = true,
    val cpuUsagePercentage: Float = 38f,
    val ramUsedMb: Long = 1850,
    val ramTotalMb: Long = 4096,
    val ramPercentage: Float = 45f,
    val batteryPercentage: Int = 85,
    val isBatteryCharging: Boolean = true
)
