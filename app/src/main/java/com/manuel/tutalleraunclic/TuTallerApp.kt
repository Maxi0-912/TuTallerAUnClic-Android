package com.manuel.tutalleraunclic

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import com.manuel.tutalleraunclic.core.navigation.Routes
import com.manuel.tutalleraunclic.ui.screens.establecimientos.EstablecimientosScreen
import  com.manuel.tutalleraunclic.ui.screens.login.LoginScreen
@Composable
fun TuTallerApp() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        // 🔐 LOGIN
        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }

        // 🏪 ESTABLECIMIENTOS (AGREGAR ESTO)
        composable(Routes.ESTABLECIMIENTOS) {
            EstablecimientosScreen(navController)
        }

        composable("cita/{id}") { backStackEntry ->

            val id = backStackEntry.arguments?.getString("id") ?: ""

            Text("Agendar cita para ID: $id")
        }
    }
}