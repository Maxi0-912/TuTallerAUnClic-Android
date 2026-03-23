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

fun NavGraphBuilder.mainGraph(navController: NavHostController) {

    composable(Routes.ESTABLECIMIENTOS) {
        MainScreen(navController)
    }

    composable(Routes.ESTABLECIMIENTOS) {
        EstablecimientosScreen(navController)
    }

    composable(Routes.MIS_CITAS) {
        MisCitasScreen(navController)
    }

    composable(Routes.MAPA) {
        MapScreen()
    }

    composable(Routes.PERFIL) {
        PerfilScreen()
    }

    composable(Routes.DETALLE_ESTABLECIMIENTO) {
        DetalleEstablecimientoScreen(
            navController = navController,
            establecimientoId = 1
        )
    }

    composable(Routes.CREAR_CITA) {
        CrearCitaScreen(
            navController = navController,
            establecimientoId = 1,
            servicioId = 1
        )
    }

}