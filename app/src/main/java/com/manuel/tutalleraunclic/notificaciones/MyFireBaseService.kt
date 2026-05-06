package com.manuel.tutalleraunclic.notifications

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.manuel.tutalleraunclic.R

class MyFirebaseService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        val title = remoteMessage.notification?.title ?: "Cita"
        val body = remoteMessage.notification?.body ?: "Nueva cita agendada"

        mostrarNotificacion(title, body)
    }

    private fun mostrarNotificacion(title: String, body: String) {

        val notification = NotificationCompat.Builder(this, "citas")
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // cámbialo si quieres
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }
}