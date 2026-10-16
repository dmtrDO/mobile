package com.example.npp_radiation_monitor.ui.components.form

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType

/**
 * Текстове поле введення встановленої потужності станції (PowerField).
 */
@Composable
fun PowerField(
    value: String,
    onValueChange: (String) -> Unit,
    error: String?
) {
    val isError = error != null

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text("Встановлена потужність (МВт)") },
        leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        isError = isError,
        supportingText = {
            if (isError) {
                Text(error)
            } else {
                Text("Значення повинно бути додатним числом")
            }
        },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )
}
