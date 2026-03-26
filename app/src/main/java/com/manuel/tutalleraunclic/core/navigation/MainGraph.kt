package com.manuel.tutalleraunclic.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.manuel.tutalleraunclic.ui.screens.main.MainScreen
import com.manuel.tutalleraunclic.ui.screens.citas.MisCitasScreen
import com.manuel.tutalleraunclic.ui.screens.citas.CrearCitaScreen
import com.manuel.tutalleraunclic.ui.screens.mapa.MapScreen
import com.manuel.tutalleraunclic.ui.screens.perfil.PerfilScreen
import com.manuel.tutalleraunclic.ui.screens.establecimientos.EstablecimientosScreen
import com.manuel.tutalleraunclic.ui.screens.establecimientos.DetalleEstablecimientoScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import  com.manuel.tutalleraunclic.viewmodel.EstablecimientoViewModel
fun NavGraphBuilder.mainGraph(navController: NavHostController) {

    // 🔹 ESTABLECIMIENTOS (pantalla principal interna)
    composable(Routes.ESTABLECIMIENTOS) {
        val viewModel: EstablecimientoViewModel = viewModel()
        EstablecimientosScreen(navController, viewModel)
    }

    // 🔹 MIS CITAS
    composable(Routes.MIS_CITAS) {
        MisCitasScreen(navController)
    }

    // 🔹 MAPA
    composable(Routes.MAPA) {
        MapScreen()
    }

    // 🔹 DETALLE ESTABLECIMIENTO
    composable("detalle_establecimiento/{id}") { backStackEntry ->

        val id = backStackEntry.arguments?.getString("id") ?: ""

        DetalleEstablecimientoScreen(
            id = id,

        )
    }

    // 🔹 CREAR CITA
    composable(Routes.CREAR_CITA) {
        CrearCitaScreen(
            navController = navController,
            establecimientoId = 1,
            servicioId = 1
        )
    }

    // 🔹 PERFIL
    composable(Routes.PERFIL) {
        PerfilScreen(
            onNavigateToLogin = {
                navController.navigate(Routes.LOGIN) {
                    popUpTo(0) // limpia todo el stack (logout real)
                }
            },
            onNavigateToEditarPerfil = {
                navController.navigate(Routes.EDITAR_PERFIL)
            }
        )
    }
}