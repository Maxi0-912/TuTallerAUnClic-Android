package com.manuel.tutalleraunclic

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import dagger.hilt.android.AndroidEntryPoint
import  com.manuel.tutalleraunclic.ui.screens.main.MainScreen
import com.manuel.tutalleraunclic.core.navigation.AppRoot
import com.manuel.tutalleraunclic.ui.theme.TuTallerAUnClicTheme
import androidx.compose.material3.NavigationBarItem

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crearCanalNotificaciones()

        setContent {
            TuTallerAUnClicTheme {
                AppRoot()
            }
        }
    }

    private fun crearCanalNotificaciones() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            val channel = NotificationChannel(
                "citas",
                "Citas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de citas"
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }
}