package com.example.npp_radiation_monitor.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.npp_radiation_monitor.navigation.Screen
import com.example.npp_radiation_monitor.ui.components.form.EmailField
import com.example.npp_radiation_monitor.ui.components.form.FormHeader
import com.example.npp_radiation_monitor.ui.components.form.PasswordField
import com.example.npp_radiation_monitor.ui.components.form.PowerField
import com.example.npp_radiation_monitor.ui.components.form.StationNameField
import com.example.npp_radiation_monitor.ui.components.form.SubmitButton
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

/**
 * Екран реєстрації та налаштування станції (StationFormScreen).
 * Реалізує комплексну форма введення даних із перевіркою (валідацією),
 * підказками користувачу (Helper Text) та індикатором завантаження (Loading State).
 */
@Composable
fun StationFormScreen(navController: NavController) {
    // Поля введення
    var stationName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var power by remember { mutableStateOf("") }

    // Повідомлення про помилки
    var stationNameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var powerError by remember { mutableStateOf<String?>(null) }

    // Стан завантаження та діалог успіху
    var isLoading by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }

    // Симуляція мережевого запиту / завантаження при відправці теми
    LaunchedEffect(isLoading) {
        if (isLoading) {
            delay(1500.milliseconds)
            isLoading = false
            showSuccessDialog = true
        }
    }

    // Функція валідації всієї форми
    fun validateForm(): Boolean {
        var isValid = true

        // 1. Валідація назви станції
        if (stationName.isBlank()) {
            stationNameError = "Заповніть назву станції"
            isValid = false
        } else {
            stationNameError = null
        }

        // 2. Валідація Email
        val emailPattern = Regex("^[A-Za-z0-9+_.-]+@(.+)$")
        if (email.isBlank()) {
            emailError = "Заповніть електронну пошту"
            isValid = false
        } else if (!email.matches(emailPattern)) {
            emailError = "Некоректний email (наприклад: operator@npp.gov.ua)"
            isValid = false
        } else {
            emailError = null
        }

        // 3. Валідація пароля
        if (password.length < 6) {
            passwordError = "Пароль повинен містити не менше 6 символів"
            isValid = false
        } else {
            passwordError = null
        }

        // 4. Валідація потужності
        val powerValue = power.toFloatOrNull()
        if (power.isBlank()) {
            powerError = "Заповніть значення потужності"
            isValid = false
        } else if (powerValue == null || powerValue <= 0) {
            powerError = "Вкажіть число більше 0 МВт"
            isValid = false
        } else {
            powerError = null
        }

        return isValid
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Заголовок форми
        FormHeader()

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Поле назви станції
        StationNameField(
            value = stationName,
            onValueChange = {
                stationName = it
                if (stationNameError != null) stationNameError = null
            },
            error = stationNameError
        )

        // Поле email
        EmailField(
            value = email,
            onValueChange = {
                email = it
                if (emailError != null) emailError = null
            },
            error = emailError
        )

        // Поле пароля
        PasswordField(
            value = password,
            onValueChange = {
                password = it
                if (passwordError != null) passwordError = null
            },
            error = passwordError
        )

        // Поле потужності
        PowerField(
            value = power,
            onValueChange = {
                power = it
                if (powerError != null) powerError = null
            },
            error = powerError
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка відправки форми
        SubmitButton(
            onClick = {
                if (validateForm()) {
                    isLoading = true
                }
            },
            isLoading = isLoading
        )

        // Кнопка скасування / повернення
        TextButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Скасувати та повернутися")
        }
    }

    // Діалог успішної реєстрації/налаштування
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Успішне збереження", style = MaterialTheme.typography.titleLarge) },
            text = { Text("Параметри станції «$stationName» та оператора «$email» успішно збережено!") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.navigate(
                            Screen.RadiationScreen.createRoute(
                                stationName = stationName,
                                operatorName = email
                            )
                        )
                    }
                ) {
                    Text("Перейти до моніторингу")
                }
            }
        )
    }
}
