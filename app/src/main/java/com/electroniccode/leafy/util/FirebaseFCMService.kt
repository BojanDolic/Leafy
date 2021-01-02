package com.electroniccode.leafy.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.electroniccode.leafy.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseFCMService : FirebaseMessagingService() {

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

        val notification = p0.notification

        notification?.let {
            createNotificationChannel()

            var notificationBuilder = NotificationCompat.Builder(this, "20")
                .setContentTitle(it.title)
                .setContentText(it.body)
                .setColor(resources.getColor(R.color.darkGreen, theme))
                .setSmallIcon(R.drawable.forum_icon)
                .setPriority(NotificationCompat.PRIORITY_HIGH)

            with(NotificationManagerCompat.from(this)) {
                notify(120, notificationBuilder.build())
            }

        }


    }

    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ime = "LeafyNotificationChannel"
            val desc = "Notification channel for leafy notifications"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("20", ime, importance).apply {
                description = desc
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}