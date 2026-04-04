package com.manuel.tutalleraunclic.core.navigation

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold

import androidx.navigation.compose.*
import androidx.navigation.NavType
import androidx.navigation.navArgument

import androidx.hilt.navigation.compose.hiltViewModel

import com.manuel.tutalleraunclic.ui.screens.login.LoginScreen
import com.manuel.tutalleraunclic.ui.screens.register.RegisterScreen
import com.manuel.tutalleraunclic.ui.screens.establecimientos.*
import com.manuel.tutalleraunclic.ui.screens.citas.*

import com.manuel.tutalleraunclic.viewmodel.*

@Composable
fun AppRoot() {

    val navController = rememberNavController()

    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    // 🔥 Control profesional del BottomBar
    val showBottomBar = remember(currentRoute) {
        when {
            currentRoute == null -> false

            currentRoute.startsWith(Routes.LOGIN) -> false
            currentRoute.startsWith(Routes.REGISTER) -> false
            currentRoute.startsWith(Routes.CREAR_CITA) -> false
            currentRoute.startsWith(Routes.DETALLE) -> false

            else -> true
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomBar(navController)
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Routes.LOGIN,
            modifier = Modifier.padding(padding)
        ) {

            // 🔐 LOGIN
            composable(Routes.LOGIN) {
                val viewModel: LoginViewModel = hiltViewModel()

                LoginScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }

            // 📝 REGISTER (🔥 FIX IMPORTANTE)
            composable(Routes.REGISTER) {
                val viewModel: RegisterViewModel = hiltViewModel()

                RegisterScreen(
                    navController = navController,
                    viewModel = viewModel
                )
            }

            // 🏢 LISTA ESTABLECIMIENTOS
            composable(Routes.ESTABLECIMIENTOS) {
                ListaEstablecimientosScreen(navController)
            }

            // 📍 DETALLE ESTABLECIMIENTO
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

            // 🔥 CREAR CITA
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

                val servicioId =
                    backStackEntry.arguments?.getInt("servicioId") ?: 0

                val viewModel: CitaViewModel = hiltViewModel()

                CrearCitaScreen(
                    viewModel = viewModel,
                    establecimientoId = establecimientoId,
                    servicioId = servicioId
                )
            }

            // 📋 MIS CITAS
            composable(Routes.MIS_CITAS) {

                val viewmodel: MisCitasViewModel = hiltViewModel()

                MisCitasScreen(
                    viewModel = viewmodel,
                    navController = navController // ✅ AQUÍ
                )
            }

            composable("editar_cita/{citaId}") { backStackEntry ->

                val citaId = backStackEntry.arguments
                    ?.getString("citaId")
                    ?.toInt() ?: 0

                EditarCitaScreen(
                    citaId = citaId
                )
            }

            // 👤 PERFIL
            composable(Routes.PERFIL) {
                // PerfilScreen()
            }

            // 🗺️ MAPA
            composable(Routes.MAPA) {
                // MapaScreen()
            }
        }
    }
}