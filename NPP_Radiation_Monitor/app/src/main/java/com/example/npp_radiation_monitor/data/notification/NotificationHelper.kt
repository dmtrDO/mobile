package com.example.npp_radiation_monitor.data.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.npp_radiation_monitor.R

/**
 * Клас управління локальними сповіщеннями та каналами повідомлень.
 */
class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_MONITORING_ID = "monitoring_channel"
        const val CHANNEL_TEMPERATURE_ID = "temperature_channel"
    }

    init {
        createNotificationChannels()
    }

    /**
     * Створення каналів сповіщень з різними пріоритетами відповідно до вимог Android.
     */
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val monitoringChannel = NotificationChannel(
                CHANNEL_MONITORING_ID,
                "сповіщення моніторингу АЕС",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "критичні сповіщення про стан радіаційного фону"
            }

            val temperatureChannel = NotificationChannel(
                CHANNEL_TEMPERATURE_ID,
                "сповіщення про температуру",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "повідомлення про зміну температурного режиму"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(monitoringChannel)
            notificationManager.createNotificationChannel(temperatureChannel)
        }
    }

    /**
     * Відправка локального сповіщення у вказаний канал.
     */
    fun sendNotification(channelId: String, title: String, message: String, notificationId: Int = System.currentTimeMillis().toInt()) {
        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(
                if (channelId == CHANNEL_MONITORING_ID)
                    NotificationCompat.PRIORITY_HIGH
                else
                    NotificationCompat.PRIORITY_DEFAULT
            )
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(context)) {
            try {
                notify(notificationId, builder.build())
            } catch (_: SecurityException) {}
        }
    }
}
