package com.manuel.tutalleraunclic.core.navigation

import androidx.navigation.*
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController

import com.manuel.tutalleraunclic.ui.screens.citas.MisCitasScreen
import com.manuel.tutalleraunclic.ui.screens.citas.CrearCitaScreen
import com.manuel.tutalleraunclic.ui.screens.mapa.MapScreen
import com.manuel.tutalleraunclic.ui.screens.perfil.PerfilScreen
import com.manuel.tutalleraunclic.ui.screens.establecimientos.EstablecimientosScreen
import com.manuel.tutalleraunclic.ui.screens.establecimientos.DetalleEstablecimientoScreen

fun NavGraphBuilder.mainGraph(navController: NavHostController) {

    // 🔹 ESTABLECIMIENTOS (HOME)
    composable(Routes.ESTABLECIMIENTOS) {
        EstablecimientosScreen(navController)
    }

    // 🔹 MIS CITAS
    composable(Routes.MIS_CITAS) {
        MisCitasScreen()
    }

    // 🔹 MAPA
    composable(Routes.MAPA) {
        MapScreen()
    }

    // 🔹 DETALLE ESTABLECIMIENTO
    composable(
        route = "${Routes.DETALLE}/{id}",
        arguments = listOf(
            navArgument("id") {
                type = NavType.StringType
            }
        )
    ) { backStackEntry ->

        val id = requireNotNull(
            backStackEntry.arguments?.getString("id")
        ) { "El id del establecimiento es obligatorio" }

        DetalleEstablecimientoScreen(
            id = id,
            navController = navController
        )
    }

    // 🔥 CREAR CITA (DINÁMICO)
    composable(
        route = "${Routes.CITA}/{establecimientoId}/{servicioId}",
        arguments = listOf(
            navArgument("establecimientoId") {
                type = NavType.IntType
            },
            navArgument("servicioId") {
                type = NavType.IntType
            }
        )
    ) { backStackEntry ->

        val establecimientoId = requireNotNull(
            backStackEntry.arguments?.getInt("establecimientoId")
        ) { "establecimientoId requerido" }

        val servicioId = requireNotNull(
            backStackEntry.arguments?.getInt("servicioId")
        ) { "servicioId requerido" }

        CrearCitaScreen(
            navController = navController,
            establecimientoId = establecimientoId,
            servicioId = servicioId
        )
    }

    // 🔹 PERFIL
    composable(Routes.PERFIL) {
        PerfilScreen(
            onNavigateToLogin = {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(0) { inclusive = true } // 🔥 limpia todo el stack
                }
            },
            onNavigateToEditarPerfil = {
                navController.navigate(Routes.EDITAR_PERFIL)
            }
        )
    }
}