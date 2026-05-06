package com.manuel.tutalleraunclic.core.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.hilt.navigation.compose.hiltViewModel

import com.manuel.tutalleraunclic.ui.screens.citas.EditarCitaScreen
import com.manuel.tutalleraunclic.ui.screens.citas.MisCitasScreen
import com.manuel.tutalleraunclic.ui.screens.citas.CrearCitaScreen
import com.manuel.tutalleraunclic.ui.screens.mapa.MapScreen
import com.manuel.tutalleraunclic.ui.screens.perfil.PerfilScreen
import com.manuel.tutalleraunclic.ui.screens.establecimientos.EstablecimientosScreen
import com.manuel.tutalleraunclic.ui.screens.establecimientos.DetalleEstablecimientoScreen
import com.manuel.tutalleraunclic.viewmodel.CitaViewModel

fun NavGraphBuilder.mainGraph(navController: NavHostController) {

    // 🔹 ESTABLECIMIENTOS (HOME)
    composable(Routes.ESTABLECIMIENTOS) {
        EstablecimientosScreen(navController)
    }

    // 🔹 MIS CITAS
    composable(Routes.MIS_CITAS) {
        MisCitasScreen(navController = navController)
    }

    // 🔹 MAPA
    composable(Routes.MAPA) {
        MapScreen(navController = navController)
    }

    // 🔹 DETALLE ESTABLECIMIENTO (TIPADO CORRECTO)
    composable(
        route = Routes.DETALLE_ARG, // 🔥 usa ruta centralizada
        arguments = listOf(
            navArgument("id") {
                type = NavType.IntType // 🔥 CAMBIO CLAVE
            }
        )
    ) { backStackEntry ->

        val establecimientoId =
            backStackEntry.arguments?.getInt("id") ?: 0

        DetalleEstablecimientoScreen(
            establecimientoId = establecimientoId, // 🔥 nombre correcto
            navController = navController
        )
    }

    // 🔥 CREAR CITA (DINÁMICO)
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
            servicioId = servicioId,
            onSuccess = {
                navController.navigate(Routes.MIS_CITAS) {
                    popUpTo(Routes.CITA) { inclusive = true }
                    launchSingleTop = true
                }
            }
        )
    }

    // 🔹 EDITAR CITA
    composable(
        route = Routes.EDITAR_CITA_ARG,
        arguments = listOf(navArgument("citaId") { type = NavType.IntType })
    ) { backStackEntry ->
        val citaId = backStackEntry.arguments?.getInt("citaId") ?: 0
        EditarCitaScreen(
            citaId = citaId,
            onSuccess = { navController.popBackStack() },
            onBack    = { navController.popBackStack() }
        )
    }

    // 🔹 EMPRESA HOME
    composable(Routes.EMPRESA_HOME) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Panel de empresa - Próximamente", style = MaterialTheme.typography.titleMedium)
        }
    }

    // 🔹 PERFIL
    composable(Routes.PERFIL) {
        PerfilScreen(
            onNavigateToLogin = {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(0) { inclusive = true } // 🔥 limpia stack completo
                }
            },
            onNavigateToEditarPerfil = {
                navController.navigate(Routes.EDITAR_PERFIL)
            }
        )
    }
}