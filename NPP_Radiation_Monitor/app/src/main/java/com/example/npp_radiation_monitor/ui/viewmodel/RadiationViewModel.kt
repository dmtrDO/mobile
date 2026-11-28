package com.example.npp_radiation_monitor.ui.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.npp_radiation_monitor.data.RadiationManager
import com.example.npp_radiation_monitor.data.auth.TokenManager
import com.example.npp_radiation_monitor.data.flow.RadiationFlowSource
import com.example.npp_radiation_monitor.data.local.AppDatabase
import com.example.npp_radiation_monitor.data.local.RadiationEntity
import com.example.npp_radiation_monitor.data.model.EnergyStation
import com.example.npp_radiation_monitor.data.model.RadiationReading
import com.example.npp_radiation_monitor.data.model.RadiationStatus
import com.example.npp_radiation_monitor.data.model.RadiationUiState
import com.example.npp_radiation_monitor.data.model.auth.AuthState
import com.example.npp_radiation_monitor.data.network.NetworkChecker
import com.example.npp_radiation_monitor.data.repository.DataSourceMode
import com.example.npp_radiation_monitor.data.repository.RadiationRepository
import com.example.npp_radiation_monitor.data.repository.SortOrder
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.sqrt

/**
 * ViewModel для управління станом моніторингу радіації (RadiationViewModel).
 */
class RadiationViewModel(application: Application) : AndroidViewModel(application) {

    private val radiationManager = RadiationManager()
    val repository: RadiationRepository

    private val _uiState = MutableStateFlow(RadiationUiState())
    val uiState: StateFlow<RadiationUiState> = _uiState.asStateFlow()

    // Для зворотної сумісності з іншими екранами
    var currentReading by mutableStateOf(radiationManager.createInitialReading())
        private set

    val allReadingsFromRoom: Flow<List<RadiationEntity>>

    private var localObserveJob: Job? = null
    private var realtimeFlowJob: Job? = null

    private val tokenManager = TokenManager(application)
    private val networkChecker = NetworkChecker(application)
    private val flowSource = RadiationFlowSource()

    init {
        val dao = AppDatabase.getDatabase(application).radiationDao()
        repository = RadiationRepository(dao)
        allReadingsFromRoom = dao.getAllReadings()

        // Початкова перевірка підключення до мережі
        val isConnected = networkChecker.isNetworkAvailable()

        // Завантаження списку енергетичних об'єктів для відображення у LazyColumn
        val initialStations = listOf(
            EnergyStation(1, "Хмельницька АЕС (Енергоблок №1)", "Атомна електростанція", "Штатний режим", 0.12f, "м. Нетішин", 50.13, 26.65),
            EnergyStation(2, "Рівненська АЕС (Пост Моніторингу №2)", "Моніторинговий пост", "Нормальний фон", 0.15f, "м. Вараш", 51.33, 25.90),
            EnergyStation(3, "Запорізький контрольний пункт", "Контрольний пункт", "Підвищена увага", 0.28f, "м. Енергодар", 47.50, 34.58),
            EnergyStation(4, "Південно-Українська АЕС (Блок №3)", "Атомна електростанція", "Штатний режим", 0.11f, "м. Південноукраїнськ", 47.81, 31.17),
            EnergyStation(5, "Чорнобильський автоматичний пост", "Автоматична станція", "Контрольований стан", 0.35f, "Зона відчуження", 51.27, 30.22)
        )

        // Перевірка наявності збереженого токена при запуску
        val savedToken = tokenManager.getToken()
        val savedEmail = tokenManager.getEmail()
        if (savedToken != null && savedEmail != null) {
            _uiState.update {
                it.copy(
                    isNetworkConnected = isConnected,
                    energyStations = initialStations,
                    authState = AuthState(
                        isAuthenticated = true,
                        token = savedToken,
                        operatorEmail = savedEmail
                    )
                )
            }
        } else {
            _uiState.update {
                it.copy(
                    isNetworkConnected = isConnected,
                    energyStations = initialStations
                )
            }
        }

        // Початкове спостереження за локальною БД Room
        observeLocalData()
    }

    /**
     * Передплата на зміни в локальній БД Room з автоматичним перерахунком метрик та фільтрації.
     */
    private fun observeLocalData() {
        localObserveJob?.cancel()
        localObserveJob = viewModelScope.launch {
            repository.localReadings.collect { readings ->
                if (_uiState.value.currentMode == DataSourceMode.LOCAL_ONLY ||
                    _uiState.value.currentMode == DataSourceMode.AGGREGATED_CACHE
                ) {
                    updateStateWithReadings(readings)
                }
            }
        }
    }

    /**
     * Перемикання режиму джерела даних (Data Source Mode).
     */
    fun selectMode(mode: DataSourceMode) {
        _uiState.update { it.copy(currentMode = mode, errorMessage = null) }
        when (mode) {
            DataSourceMode.LOCAL_ONLY -> observeLocalData()
            DataSourceMode.REMOTE_ONLY -> fetchRemoteData()
            DataSourceMode.AGGREGATED_CACHE -> syncRemoteWithLocal()
        }
    }

    /**
     * Отримання даних з віддаленого джерела (Remote API).
     */
    fun fetchRemoteData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.getRemoteReadings()
            if (result.isSuccess) {
                val remoteReadings = result.getOrDefault(emptyList())
                updateStateWithReadings(remoteReadings)
            } else {
                val err = result.exceptionOrNull()?.message ?: "Помилка завантаження даних із сервера"
                _uiState.update { it.copy(isLoading = false, errorMessage = err) }
            }
        }
    }

    /**
     * Синхронізація віддалених даних із кешем Room (AGGREGATED_CACHE).
     */
    fun syncRemoteWithLocal() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.syncRemoteWithLocalCache()
            if (result.isFailure) {
                val err = result.exceptionOrNull()?.message ?: "Помилка синхронізації з віддаленим сервером"
                _uiState.update { it.copy(isLoading = false, errorMessage = err) }
            } else {
                observeLocalData()
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    /**
     * Генерація наступного вимірювання датчиком та збереження у базі Room.
     */
    fun generateNextReading(): RadiationReading {
        val newReading = radiationManager.generateNextReading()
        currentReading = newReading

        viewModelScope.launch {
            repository.addReading(
                value = newReading.value,
                timestamp = newReading.timestamp,
                saveToRemote = (_uiState.value.currentMode == DataSourceMode.REMOTE_ONLY)
            )
            if (_uiState.value.currentMode == DataSourceMode.REMOTE_ONLY) {
                fetchRemoteData()
            }
        }
        return newReading
    }

    /**
     * Ручне додавання вимірювання з довільними параметрами.
     */
    fun addCustomReading(value: Float, timestamp: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = repository.addReading(
                value = value,
                timestamp = timestamp,
                saveToRemote = (_uiState.value.currentMode == DataSourceMode.REMOTE_ONLY)
            )
            if (result.isFailure) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false) }
                if (_uiState.value.currentMode == DataSourceMode.REMOTE_ONLY) {
                    fetchRemoteData()
                }
            }
        }
    }

    /**
     * Оновлення існуючого заміру за ідентифікатором (CRUD - Update).
     */
    fun updateReading(id: Int, value: Float, timestamp: String) {
        viewModelScope.launch {
            val result = repository.updateReading(id, value, timestamp)
            if (result.isFailure) {
                _uiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message) }
            }
        }
    }

    /**
     * Видалення замеров за ідентифікатором (CRUD - Delete).
     */
    fun deleteReading(id: Int) {
        viewModelScope.launch {
            val result = repository.deleteReading(id)
            if (result.isFailure) {
                _uiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message) }
            }
        }
    }

    /**
     * Очищення всієї історії вимірювань.
     */
    fun clearAllReadings() {
        viewModelScope.launch {
            val result = repository.clearAllReadings()
            if (result.isFailure) {
                _uiState.update { it.copy(errorMessage = result.exceptionOrNull()?.message) }
            }
        }
    }

    /**
     * Встановлення тексту пошуку.
     */
    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        reapplyFilterAndSort()
    }

    /**
     * Фільтрація за статусом радіації (Безпечно, Увага, Небезпека).
     */
    fun setStatusFilter(status: RadiationStatus?) {
        _uiState.update { it.copy(statusFilter = status) }
        reapplyFilterAndSort()
    }

    /**
     * Зміна порядку сортування.
     */
    fun setSortOrder(sortOrder: SortOrder) {
        _uiState.update { it.copy(sortOrder = sortOrder) }
        reapplyFilterAndSort()
    }

    /**
     * Перемикач симуляції мережевої помилки для тестування обробки помилок.
     */
    fun toggleRemoteErrorSimulation(enable: Boolean) {
        repository.toggleRemoteErrorSimulation(enable)
        _uiState.update { it.copy(isRemoteErrorSimulated = repository.isRemoteErrorSimulated()) }
    }

    /**
     * Закриття повідомлення про помилку.
     */
    fun dismissError() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    /**
     Отримання та десеріалізація JSON даних з REST API через Retrofit і Gson.
     */
    fun fetchRestApiData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = repository.fetchRestApiForecast()
            if (result.isSuccess) {
                val apiResponse = result.getOrNull()
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        restApiData = apiResponse
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = result.exceptionOrNull()?.message ?: "Помилка виконання REST API запиту"
                    )
                }
            }
        }
    }

    /**
     * Оновлення стану UI на основі списку замірів та бізнес-логіки репозиторію.
     */
    private fun updateStateWithReadings(readings: List<RadiationReading>) {
        val metrics = repository.calculateMetrics(readings)
        val filtered = repository.filterAndSortReadings(
            readings = readings,
            searchQuery = _uiState.value.searchQuery,
            statusFilter = _uiState.value.statusFilter,
            sortOrder = _uiState.value.sortOrder
        )
        _uiState.update {
            it.copy(
                rawReadings = readings,
                filteredReadings = filtered,
                metrics = metrics,
                isLoading = false,
                isRemoteErrorSimulated = repository.isRemoteErrorSimulated()
            )
        }
    }

    /**
     * Автентифікація користувача, генерація та збереження токена.
     */
    fun loginMockedUser(email: String, password: String): Boolean {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update {
                it.copy(
                    authState = it.authState.copy(
                        isAuthenticated = false,
                        errorMessage = "Поля логіну та пароля не можуть бути порожніми!"
                    )
                )
            }
            return false
        }

        if (password.length < 6) {
            _uiState.update {
                it.copy(
                    authState = it.authState.copy(
                        isAuthenticated = false,
                        errorMessage = "Пароль повинен бути довжиною від 6 символів!"
                    )
                )
            }
            return false
        }

        val mockToken = tokenManager.generateMockToken(email)
        tokenManager.saveAuthData(mockToken, email)

        _uiState.update {
            it.copy(
                authState = AuthState(
                    isAuthenticated = true,
                    token = mockToken,
                    operatorEmail = email,
                    errorMessage = null
                )
            )
        }
        return true
    }

    /**
     * Вихід з облікового запису та очищення токена.
     */
    fun logoutUser() {
        tokenManager.clearAuthData()
        _uiState.update {
            it.copy(
                authState = AuthState(
                    isAuthenticated = false,
                    token = null,
                    operatorEmail = null,
                    errorMessage = null
                )
            )
        }
    }

    /**
     * Перевірка інтернет-з'єднання перед запитом та інформування користувача.
     */
    fun checkNetworkConnection(): Boolean {
        val isConnected = networkChecker.isNetworkAvailable()
        if (!isConnected) {
            _uiState.update {
                it.copy(
                    isNetworkConnected = false,
                    networkStatusMessage = "Помилка мережі: Відсутнє підключення до інтернету! Перевірте з'єднання."
                )
            }
            return false
        } else {
            _uiState.update {
                it.copy(
                    isNetworkConnected = true,
                    networkStatusMessage = "Мережу підключено. З'єднання з сервером АЕС стабільне."
                )
            }
            return true
        }
    }

    /**
     * Перемикач симуляції офлайн-режиму для тестування обробки помилок у UI.
     */
    fun toggleSimulatedOffline(forceOffline: Boolean) {
        networkChecker.toggleSimulatedOffline(forceOffline)
        val isConnected = networkChecker.isNetworkAvailable()
        _uiState.update {
            it.copy(
                isSimulatedOffline = forceOffline,
                isNetworkConnected = isConnected,
                networkStatusMessage = if (!isConnected) "Режим 'Без інтернету' увімкнено! Мережеві запити блокуються." else "Мережу відновлено."
            )
        }
    }

    /**
     * Закриття повідомлення про статус мережі.
     */
    fun dismissNetworkStatus() {
        _uiState.update { it.copy(networkStatusMessage = null) }
    }

    /**
     * Запускає асинхронний потік Flow із використанням операторів filter, map, catch.
     */
    fun startRealtimeFlowStream() {
        if (realtimeFlowJob?.isActive == true) return

        realtimeFlowJob = viewModelScope.launch {
            _uiState.update { it.copy(isFlowActive = true) }
            flowSource.getRadiationSensorStream().collect { reading ->
                if (reading.id == -1) {
                    _uiState.update {
                        it.copy(
                            errorMessage = "Оператор Flow.catch: Перехоплено помилку датчика у потоці!"
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            realtimeReading = reading,
                            flowEmittedCount = it.flowEmittedCount + 1
                        )
                    }
                }
            }
        }
    }

    /**
     * Зупиняє асинхронний потік Flow.
     */
    fun stopRealtimeFlowStream() {
        realtimeFlowJob?.cancel()
        realtimeFlowJob = null
        _uiState.update { it.copy(isFlowActive = false) }
    }

    /**
     * Вибір елемента списку для перегляду детальної інформації.
     */
    fun selectDetailReading(reading: RadiationReading?) {
        _uiState.update { it.copy(selectedDetailReading = reading) }
    }

    /**
     * Закриття детального вікна перегляду елемента.
     */
    fun dismissDetailView() {
        _uiState.update { it.copy(selectedDetailReading = null) }
    }

    /**
     * Вибір станції на мапі.
     */
    fun selectMapStation(station: EnergyStation?) {
        _uiState.update { it.copy(selectedMapStation = station) }
    }

    /**
     * Оновлення поточних координат GPS користувача.
     */
    fun updateUserGpsLocation(lat: Double, lng: Double) {
        _uiState.update { it.copy(userLatitude = lat, userLongitude = lng) }
    }

    /**
     * Оновлення показників акселерометра пристрою в реальному часі.
     */
    fun updateSensorValues(x: Float, y: Float, z: Float) {
        val magnitude = sqrt(x * x + y * y + z * z)
        _uiState.update {
            it.copy(
                accelerometerX = x,
                accelerometerY = y,
                accelerometerZ = z,
                accelerometerMagnitude = (magnitude * 100).toInt() / 100f
            )
        }
    }

    /**
     * Оновлення показника гіроскопа.
     */
    fun updateGyroscopeValue(z: Float) {
        _uiState.update { it.copy(gyroscopeZ = z) }
    }

    /**
     * Оновлення поточних ресурсів пристрою у реальному часі.
     */
    fun updateSystemResources(cpu: Float, ramUsed: Long, ramTotal: Long, batteryPct: Int, isCharging: Boolean) {
        val ramPct = if (ramTotal > 0) (ramUsed.toFloat() / ramTotal.toFloat()) * 100f else 0f
        _uiState.update {
            it.copy(
                cpuUsagePercentage = cpu,
                ramUsedMb = ramUsed,
                ramTotalMb = ramTotal,
                ramPercentage = ramPct,
                batteryPercentage = batteryPct,
                isBatteryCharging = isCharging
            )
        }
    }

    private fun reapplyFilterAndSort() {
        val filtered = repository.filterAndSortReadings(
            readings = _uiState.value.rawReadings,
            searchQuery = _uiState.value.searchQuery,
            statusFilter = _uiState.value.statusFilter,
            sortOrder = _uiState.value.sortOrder
        )
        _uiState.update { it.copy(filteredReadings = filtered) }
    }
}
