package com.example.npp_radiation_monitor.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

fun checkErrors(email: String, password: String): String? {
    return when {
        email.isBlank() -> "Заповніть електронну пошту"
        password.length < 6 -> "Пароль повинен містити більше 6 символів"
        else -> null
    }
}

@Composable
fun Errors(error: String?) {
    if (error == null) return
    if (error.isNotEmpty()) {
        Text(
            text = error,
            color = Color.Red,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun AuthDialog(
    onDismissRequest: () -> Unit,
    onSuccess: (operatorName: String) -> Unit
) {
    // Перемикач режимів: false = вхід, true = реєстрація
    var isRegisterMode by remember { mutableStateOf(false) }

    // Поля введення
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Булева змінна, яка каже, чи показувати помилку, чи ні
    var showError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = if (isRegisterMode) "Реєстрація оператора АЕС" else "Вхід у систему АЕС",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Поле імені (показується тільки при реєстрації)
                if (isRegisterMode) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it; showError = false },
                        label = { Text("ПІБ Оператора") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Поле email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; showError = false },
                    label = { Text("Електронна пошта") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Поле password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it; showError = false },
                    label = { Text("Пароль") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (showError) Errors(checkErrors(email, password))

                Spacer(modifier=Modifier.height(8.dp))

                // Кнопка перемикання вхід / реєстрація
                TextButton(
                    onClick = { isRegisterMode = !isRegisterMode; showError = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isRegisterMode) "Вже є акаунт? Увійти" else "Немає акаунту? Зареєструватися"
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    // Перевіряємо, чи є помилки
                    if (checkErrors(email, password) != null) {
                        showError = true
                    } else {
                        onSuccess(email)
                    }
                }
            ) {
                Text(if (isRegisterMode) "Зареєструватися" else "Увійти")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Скасувати")
            }
        }
    )
}