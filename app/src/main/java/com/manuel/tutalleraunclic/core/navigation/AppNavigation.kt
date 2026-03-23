package com.manuel.tutalleraunclic.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavHostController
import androidx.compose.material3.Text

import com.manuel.tutalleraunclic.ui.screens.main.MainScreen
import com.manuel.tutalleraunclic.ui.screens.login.LoginScreen
import com.manuel.tutalleraunclic.ui.screens.establecimientos.DetalleEstablecimientoScreen

@Composable
fun AppNavigation(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        // 🔐 LOGIN
        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }

        // 🏠 MAIN
        composable(Routes.ESTABLECIMIENTOS) {
            MainScreen(navController)
        }

        // 🏢 DETALLE
        composable(
            route = Routes.DETALLE_ESTABLECIMIENTO,
            arguments = listOf(
                navArgument("id") { type = NavType.IntType }
            )
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getInt("id") ?: 0

            DetalleEstablecimientoScreen(
                navController = navController,
                establecimientoId = id
            )
        }

        // 🔥 ESTAS TE FALTAN
        composable(Routes.MIS_CITAS) {
            Text("Pantalla Mis Citas")
        }

        composable(Routes.MAPA) {
            Text("Pantalla Mapa")
        }

        composable(Routes.PERFIL) {
            Text("Pantalla Perfil")
        }
    }
}