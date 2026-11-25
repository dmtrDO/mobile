package com.example.npp_radiation_monitor.data.model.auth

/**
 * Стан автентифікації користувача.
 * Зберігає статус входу, токен та електронну пошту оператора.
 */
data class AuthState(
    val isAuthenticated: Boolean = false,
    val token: String? = null,
    val operatorEmail: String? = null,
    val errorMessage: String? = null
)
