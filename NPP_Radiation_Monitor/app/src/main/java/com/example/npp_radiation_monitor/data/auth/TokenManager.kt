package com.example.npp_radiation_monitor.data.auth

import android.content.Context
import android.content.SharedPreferences

/**
 * Менеджер для управління збереженням токена у SharedPreferences.
 */
class TokenManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("npp_auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_TOKEN = "auth_token"
        private const val KEY_EMAIL = "operator_email"
    }

    /**
     * Генерація тестового mocked JWT токена.
     */
    fun generateMockToken(email: String): String {
        val timestamp = System.currentTimeMillis()
        val mockJwtHeader = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        val mockPayload = "eyJzdWIiOiJ$email\",\"iat\":$timestamp}"
        val mockSignature = "SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c"
        return "$mockJwtHeader.$mockPayload.$mockSignature"
    }

    /**
     * Збереження токена та email у локальне сховище.
     */
    fun saveAuthData(token: String, email: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_EMAIL, email)
            .apply()
    }

    /**
     * Отримання збереженого токена.
     */
    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    /**
     * Отримання збереженого email.
     */
    fun getEmail(): String? = prefs.getString(KEY_EMAIL, null)

    /**
     * Очищення сховища при виході.
     */
    fun clearAuthData() {
        prefs.edit().clear().apply()
    }
}
