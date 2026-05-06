package com.manuel.tutalleraunclic

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.*
import com.manuel.tutalleraunclic.core.navigation.AppRoot
import com.manuel.tutalleraunclic.core.navigation.resolveHomeRoute
import com.manuel.tutalleraunclic.core.navigation.Routes
import com.manuel.tutalleraunclic.data.local.TokenManager
import com.manuel.tutalleraunclic.ui.components.SplashScreen
import com.manuel.tutalleraunclic.ui.theme.TuTallerAUnClicTheme
import com.manuel.tutalleraunclic.viewmodel.ThemeViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var tokenManager: TokenManager

    private val themeViewModel: ThemeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        crearCanalNotificaciones()

        setContent {
            val isDark by themeViewModel.isDarkMode.collectAsState()

            TuTallerAUnClicTheme(
                darkTheme = isDark,
                onToggleTheme = { themeViewModel.toggle() }
            ) {
                var showSplash by remember { mutableStateOf(true) }
                val isLoggedIn = remember { tokenManager.hasValidSession() }
                val startRoute = when {
                    !isLoggedIn -> Routes.LOGIN
                    tokenManager.getRolNombre() == "empresa" -> Routes.EMPRESA_HOME
                    else -> Routes.ESTABLECIMIENTOS
                }

                if (showSplash) {
                    SplashScreen { showSplash = false }
                } else {
                    AppRoot(
                        isLoggedIn = isLoggedIn,
                        startRoute = startRoute
                    )
                }
            }
        }
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "citas",
                "Citas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Notificaciones de citas" }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }
    }
}
