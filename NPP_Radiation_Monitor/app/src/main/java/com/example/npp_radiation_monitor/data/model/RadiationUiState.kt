package com.example.npp_radiation_monitor.data.model

import com.example.npp_radiation_monitor.data.model.api.RadiationForecastResponse
import com.example.npp_radiation_monitor.data.model.auth.AuthState
import com.example.npp_radiation_monitor.data.repository.DataSourceMode
import com.example.npp_radiation_monitor.data.repository.SortOrder

/**
 * Стан користувацького інтерфейсу (UI State) для екрана моніторингу та історії радіації.
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
    val networkStatusMessage: String? = null
)
