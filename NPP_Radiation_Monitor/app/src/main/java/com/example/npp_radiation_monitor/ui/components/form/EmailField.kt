package com.example.npp_radiation_monitor.ui.components.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

/**
 * Текстове поле введення email оператора (EmailField).
 */
@Composable
fun EmailField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?
) {
    val isError = error != null

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Електронна пошта оператора") },
        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        isError = isError,
        supportingText = {
            if (isError) {
                Text(error)
            } else {
                Text("Формат: operator@npp.gov.ua")
            }
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}
