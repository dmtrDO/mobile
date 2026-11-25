package com.example.npp_radiation_monitor.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.npp_radiation_monitor.data.model.RadiationReading
import com.example.npp_radiation_monitor.data.model.RadiationStatus
import com.example.npp_radiation_monitor.data.repository.DataSourceMode
import com.example.npp_radiation_monitor.data.repository.SortOrder
import com.example.npp_radiation_monitor.navigation.Screen
import com.example.npp_radiation_monitor.ui.components.HistoryItem
import com.example.npp_radiation_monitor.ui.viewmodel.RadiationViewModel
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Екран історії та розширеного управління даними.
 */
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryScreen(
    navController: NavController,
    viewModel: RadiationViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Стан діалогів для CRUD операцій
    var showAddDialog by remember { mutableStateOf(false) }
    var editingReading by remember { mutableStateOf<RadiationReading?>(null) }
    var showDeleteConfirmId by remember { mutableStateOf<Int?>(null) }
    var showClearAllConfirm by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Кнопка навігації
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(
                onClick = { navController.navigate(Screen.MainScreen.route) },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Головна", fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.weight(1f)
            ) {
                Text(text = "Моніторинг", fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Таби вибору режиму Data Source (Локально / Віддалено / Агрегований кеш)
        Text(
            text = "Режим Data Source:",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Start)
                .padding(bottom = 4.dp)
        )

        val modes = DataSourceMode.entries
        val selectedTabIndex = modes.indexOf(uiState.currentMode)

        TabRow(
            selectedTabIndex = selectedTabIndex,
            modifier = Modifier.fillMaxWidth()
        ) {
            modes.forEachIndexed { index, mode ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { viewModel.selectMode(mode) },
                    text = {
                        Text(
                            text = when (mode) {
                                DataSourceMode.LOCAL_ONLY -> "Room БД"
                                DataSourceMode.REMOTE_ONLY -> "API Мережа"
                                DataSourceMode.AGGREGATED_CACHE -> "Кеш + Мережа"
                            },
                            fontSize = 12.sp,
                            maxLines = 1
                        )
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Панель управління кешем та симуляція помилок
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (uiState.currentMode == DataSourceMode.AGGREGATED_CACHE) {
                OutlinedButton(
                    onClick = { viewModel.syncRemoteWithLocal() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Синхронізувати кеш", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
            } else if (uiState.currentMode == DataSourceMode.REMOTE_ONLY) {
                OutlinedButton(
                    onClick = { viewModel.fetchRemoteData() },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Оновити з API", fontSize = 12.sp)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }

            // Перемикач для сумуляції мережевої помилки (для демонстрації обробки помилок)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text("Помилка API:", fontSize = 11.sp, color = Color.Gray)
                Spacer(modifier = Modifier.width(4.dp))
                Switch(
                    checked = uiState.isRemoteErrorSimulated,
                    onCheckedChange = { viewModel.toggleRemoteErrorSimulation(it) }
                )
            }
        }

        // Відображення банера помилки, якщо вона виникла під час виконання запиту в Repository
        if (uiState.errorMessage != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    IconButton(onClick = { viewModel.dismissError() }) {
                        Icon(Icons.Default.Clear, contentDescription = "Закрити")
                    }
                }
            }
        }

        // Агрегована аналітика бізнес-рівня
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Аналітика Repository (Бізнес-агрегація)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = String.format(Locale.UK, "Сер. рівень: %.2f мкЗв/год", uiState.metrics.averageValue),
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = String.format(Locale.UK, "Макс. рівень: %.2f мкЗв/год", uiState.metrics.maxValue),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Column {
                        Text(
                            text = "Всього замірів: ${uiState.metrics.totalCount}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Тривоги: ${uiState.metrics.dangerCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (uiState.metrics.dangerCount > 0) MaterialTheme.colorScheme.error else Color.Unspecified
                        )
                    }
                }
            }
        }

        // Інтеграція REST API + Retrofit + Gson
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "REST API (Retrofit + Gson)",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Button(
                        onClick = { viewModel.fetchRestApiData() },
                        modifier = Modifier.height(36.dp)
                    ) {
                        Text("Запит JSON", fontSize = 11.sp)
                    }
                }

                if (uiState.restApiData != null) {
                    val apiData = uiState.restApiData!!
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Станція: ${apiData.stationName} (${apiData.stationId})",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Статус REST API: ${apiData.status} | Потужність: ${apiData.forecastPowerMw} МВт",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Рівень радіації (JSON): ${apiData.radiationLevelUsv} мкЗв/год [Оновлено: ${apiData.lastUpdated}]",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Автентифікація через API (Тестовий Token/JWT)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (uiState.authState.isAuthenticated) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Автентифікація через API (Тестовий Token / JWT)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                if (uiState.authState.isAuthenticated) {
                    Text(
                        text = "Статус: Успішно авторизовано",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Оператор: ${uiState.authState.operatorEmail}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Виданий Токен: ${uiState.authState.token?.take(32)}...",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedButton(
                        onClick = { viewModel.logoutUser() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                    ) {
                        Text("Вийти з системи (Видалити Токен)", fontSize = 12.sp)
                    }
                } else {
                    var inputEmail by remember { mutableStateOf("") }
                    var inputPassword by remember { mutableStateOf("") }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = inputEmail,
                            onValueChange = { inputEmail = it },
                            placeholder = { Text("Логін / Email", fontSize = 14.sp) },
                            modifier = Modifier
                                .fillMaxWidth(),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = inputPassword,
                            onValueChange = { inputPassword = it },
                            placeholder = { Text("Пароль (мін 6 символів)", fontSize = 14.sp) },
                            modifier = Modifier
                                .fillMaxWidth(),
                            singleLine = true
                        )

                        if (uiState.authState.errorMessage != null) {
                            Text(
                                text = uiState.authState.errorMessage ?: "",
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        Button(
                            onClick = {
                                viewModel.loginMockedUser(inputEmail, inputPassword)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(38.dp)
                        ) {
                            Text("Вхід (Згенерувати Токен)", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // Обробка помилок мережевих запитів та інформування користувача
        if (uiState.networkStatusMessage != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (uiState.isNetworkConnected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .padding(10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (uiState.isNetworkConnected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = uiState.networkStatusMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (uiState.isNetworkConnected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                    IconButton(onClick = { viewModel.dismissNetworkStatus() }) {
                        Icon(Icons.Default.Clear, contentDescription = "Закрити")
                    }
                }
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Обробка помилок мережі (Network Check)",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (uiState.isNetworkConnected) "Підключення: АКТИВНЕ" else "Підключення: ВІДСУТНЄ",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (uiState.isNetworkConnected) Color(0xFF4CAF50) else Color(0xFFF44336)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Симуляція офлайн:", fontSize = 11.sp, color = Color.Gray)
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = uiState.isSimulatedOffline,
                            onCheckedChange = { viewModel.toggleSimulatedOffline(it) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Button(
                    onClick = { viewModel.checkNetworkConnection() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                ) {
                    Text("Перевірити з'єднання та стан мережі", fontSize = 12.sp)
                }
            }
        }

        // Панель пошуку та дій (Додати, Очистити, Сортувати)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                placeholder = { Text("Пошук за часом/статусом...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(4.dp))

            // Кнопка сортування
            Box {
                IconButton(onClick = { showSortMenu = true }) {
                    Icon(Icons.Default.FilterList, contentDescription = "Сортування")
                }
                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false }
                ) {
                    SortOrder.entries.forEach { order ->
                        DropdownMenuItem(
                            text = { Text(order.title) },
                            onClick = {
                                viewModel.setSortOrder(order)
                                showSortMenu = false
                            }
                        )
                    }
                }
            }

            // Додати замір
            IconButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Додати замір", tint = MaterialTheme.colorScheme.primary)
            }

            // Очистити всі
            IconButton(onClick = { showClearAllConfirm = true }) {
                Icon(Icons.Default.Delete, contentDescription = "Очистити все", tint = MaterialTheme.colorScheme.error)
            }
        }

        // Фільтраційні чіпи за статусом радіаційної безпеки
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                FilterChip(
                    selected = uiState.statusFilter == null,
                    onClick = { viewModel.setStatusFilter(null) },
                    label = { Text("Усі (${uiState.rawReadings.size})", fontSize = 11.sp) }
                )
            }
            items(RadiationStatus.entries) { status ->
                FilterChip(
                    selected = uiState.statusFilter == status,
                    onClick = { viewModel.setStatusFilter(status) },
                    label = { Text(status.title, fontSize = 11.sp) }
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        // Список вимірювань або індикатор завантаження
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.filteredReadings.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Записи відсутні або не відповідають фільтру",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(uiState.filteredReadings, key = { it.id }) { reading ->
                    HistoryItem(
                        reading = reading,
                        onEdit = { editingReading = reading },
                        onDelete = { showDeleteConfirmId = reading.id }
                    )
                }
            }
        }
    }

    // --- ДІАЛОГИ УПРАВЛІННЯ ДАНИМИ (CRUD) ---

    // 1. Діалог додавання нового заміру
    if (showAddDialog) {
        var valueText by remember { mutableStateOf("0.25") }
        var timestampText by remember {
            mutableStateOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")))
        }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Додати новий замір радіації") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = valueText,
                        onValueChange = { valueText = it },
                        label = { Text("Показник (мкЗв/год)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = timestampText,
                        onValueChange = { timestampText = it },
                        label = { Text("Час (HH:mm:ss)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val valFloat = valueText.toFloatOrNull() ?: 0.15f
                        viewModel.addCustomReading(valFloat, timestampText)
                        showAddDialog = false
                    }
                ) {
                    Text("Зберегти")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Скасувати")
                }
            }
        )
    }

    // 2. Діалог редагування вимірювання (CRUD - Update)
    if (editingReading != null) {
        val target = editingReading!!
        var valueText by remember { mutableStateOf(target.value.toString()) }
        var timestampText by remember { mutableStateOf(target.timestamp) }

        AlertDialog(
            onDismissRequest = { editingReading = null },
            title = { Text("Редагувати замір #${target.id}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = valueText,
                        onValueChange = { valueText = it },
                        label = { Text("Показник (мкЗв/год)") },
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = timestampText,
                        onValueChange = { timestampText = it },
                        label = { Text("Час (HH:mm:ss)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val valFloat = valueText.toFloatOrNull() ?: target.value
                        viewModel.updateReading(target.id, valFloat, timestampText)
                        editingReading = null
                    }
                ) {
                    Text("Оновити")
                }
            },
            dismissButton = {
                TextButton(onClick = { editingReading = null }) {
                    Text("Скасувати")
                }
            }
        )
    }

    // 3. Діалог підтвердження видалення елемента (CRUD - Delete)
    if (showDeleteConfirmId != null) {
        val targetId = showDeleteConfirmId!!
        AlertDialog(
            onDismissRequest = { showDeleteConfirmId = null },
            title = { Text("Видалення вимірювання") },
            text = { Text("Ви дійсно бажаєте видалити замір #$targetId?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteReading(targetId)
                        showDeleteConfirmId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Видалити")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmId = null }) {
                    Text("Скасувати")
                }
            }
        )
    }

    // 4. Діалог очищення всієї історії
    if (showClearAllConfirm) {
        AlertDialog(
            onDismissRequest = { showClearAllConfirm = false },
            title = { Text("Очистити всю історію?") },
            text = { Text("Це видалить усі збережені записи вимірювань. Дія незворотна.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllReadings()
                        showClearAllConfirm = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Очистити все")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearAllConfirm = false }) {
                    Text("Скасувати")
                }
            }
        )
    }
}
