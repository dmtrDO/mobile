package com.example.npp_radiation_monitor.ui.components.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Текстове поле введення назви станції/блоку АЕС (StationNameField).
*/
@Composable
fun StationNameField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?
) {
    val isError = error != null

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Назва станції / Блоку") },
        leadingIcon = { Icon(Icons.Default.Home, contentDescription = null) },
        isError = isError,
        supportingText = {
            if (isError) {
                Text(error)
            } else {
                Text("Наприклад: Запорізька АЕС, Блок №1")
            }
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}
