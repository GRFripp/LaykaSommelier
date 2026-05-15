package com.example.laykasommelier.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.laykasommelier.MainActivity
import com.example.laykasommelier.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Показываем уведомление только если приложение в фоне или свёрнуто
        // (в foreground можно показывать свой UI, но мы используем системное)
        showNotification(remoteMessage.notification?.title ?: "Новая заявка",
            remoteMessage.notification?.body ?: "")
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        // Если нужно сохранять токен на сервере – здесь.
        // Для подписки на топики достаточно, токен не обязателен.
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "suggestions_channel"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаём канал уведомлений (Android 8+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Заявки на коктейли",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Уведомления о новых заявках"
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Intent, который откроет MainActivity и передаст флаг открытия заявок
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra("open_suggestions", true)
        }
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Строим уведомление
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_launcher_background)  // замените на свой значок
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(1, notification)
    }
}
