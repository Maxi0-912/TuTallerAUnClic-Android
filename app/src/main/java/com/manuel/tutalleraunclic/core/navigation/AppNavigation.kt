package com.manuel.tutalleraunclic.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import androidx.hilt.navigation.compose.hiltViewModel
import com.manuel.tutalleraunclic.ui.screens.login.LoginScreen
import com.manuel.tutalleraunclic.ui.screens.establecimientos.ListaEstablecimientosScreen
import com.manuel.tutalleraunclic.ui.screens.establecimientos.DetalleEstablecimientoScreen
import com.manuel.tutalleraunclic.ui.screens.citas.CrearCitaScreen
import com.manuel.tutalleraunclic.viewmodel.LoginViewModel
import  com.manuel.tutalleraunclic.viewmodel.CitaViewModel
@Composable
fun AppNavigation() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        // 🔐 LOGIN
        composable(Routes.LOGIN) {
            val viewModel: LoginViewModel = hiltViewModel()

            LoginScreen(
                navController = navController,
                viewModel = viewModel
            )
        }

        // 🏢 LISTA DE ESTABLECIMIENTOS
        composable(Routes.ESTABLECIMIENTOS) {
            ListaEstablecimientosScreen(navController)
        }

        // 📍 DETALLE (CON ARGUMENTO ID)
        composable(
            route = Routes.DETALLE_ARG,
            arguments = listOf(
                navArgument("id") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getString("id") ?: ""

            DetalleEstablecimientoScreen(
                id = id,
                navController = navController
            )
        }

        // 🔥 CREAR CITA (CON 2 ARGUMENTOS)
        composable(
            route = Routes.CITA_ARG,
            arguments = listOf(
                navArgument("establecimientoId") {
                    type = NavType.IntType
                },
                navArgument("servicioId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->

            val establecimientoId =
                backStackEntry.arguments?.getInt("establecimientoId") ?: 0

            val viewModel: CitaViewModel = hiltViewModel()

            CrearCitaScreen(
                viewModel = viewModel,
                establecimientoId = establecimientoId
            )
        }
    }
}