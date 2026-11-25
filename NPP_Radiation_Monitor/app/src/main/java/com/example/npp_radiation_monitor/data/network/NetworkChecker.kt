package com.example.npp_radiation_monitor.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

/**
 * Клас перевірки підключення до мережі інтернет (NetworkChecker).
 * Дозволяє перевіряти наявність інтернет-з'єднання та підтримує симуляцію режиму "Офлайн".
 */
class NetworkChecker(private val context: Context) {

    private var isSimulatedOffline: Boolean = false

    /**
     * Перевіряє наявність підключення до інтернету через ConnectivityManager.
     */
    fun isNetworkAvailable(): Boolean {
        if (isSimulatedOffline) return false

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    }

    /**
     * Перемикач для симуляції відсутності мережі при тестуванні обробки помилок.
     */
    fun toggleSimulatedOffline(forceOffline: Boolean) {
        isSimulatedOffline = forceOffline
    }

    fun isSimulatedOffline(): Boolean = isSimulatedOffline
}
