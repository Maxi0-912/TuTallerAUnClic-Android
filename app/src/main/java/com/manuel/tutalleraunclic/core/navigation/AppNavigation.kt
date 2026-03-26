package com.manuel.tutalleraunclic.core.navigation


import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavHostController
import androidx.compose.material3.Text
import androidx.hilt.navigation.compose.hiltViewModel
import com.manuel.tutalleraunclic.ui.screens.perfil.PerfilScreen
import com.manuel.tutalleraunclic.ui.screens.perfil.EditarPerfilScreen
import com.manuel.tutalleraunclic.ui.screens.main.MainScreen
import com.manuel.tutalleraunclic.ui.screens.login.LoginScreen
import com.manuel.tutalleraunclic.ui.screens.establecimientos.DetalleEstablecimientoScreen
import com.manuel.tutalleraunclic.viewmodel.EstablecimientoViewModel
import com.manuel.tutalleraunclic.ui.screens.establecimientos.EstablecimientosScreen
import com.manuel.tutalleraunclic.ui.screens.establecimientos.ListaEstablecimientosScreen
import com.manuel.tutalleraunclic.viewmodel.LoginViewModel
import  com.manuel.tutalleraunclic.ui.screens.citas.CrearCitaScreen

@Composable
fun AppNavigation(navController: NavHostController) {

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {

        // 🔐 LOGIN
        composable("login") {
            LoginScreen(navController)
        }


        @Composable
        fun LoginScreen() {
            val viewModel: LoginViewModel = hiltViewModel()
        }


        composable(Routes.LOGIN) {
            LoginScreen(navController)
        }

        // 🏠 MAIN
        composable(Routes.ESTABLECIMIENTOS) {
            MainScreen()
        }

        // 🏢 LISTA
        composable("lista_establecimientos") {
            ListaEstablecimientosScreen(navController)
        }

        // 🏢 DETALLE (UNIFICADO)
        composable(
            route = "detalle_establecimiento/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.StringType }
            )
        ) { backStackEntry ->

            val id = backStackEntry.arguments?.getString("id") ?: return@composable

            DetalleEstablecimientoScreen(
                id = id
            )
        }

        // 📌 MIS CITAS
        composable(Routes.MIS_CITAS) {
            Text("Pantalla Mis Citas")
        }

        composable("${Routes.CITA}/{id}") { backStackEntry ->

            val id = backStackEntry.arguments?.getString("id") ?: ""

            Text("Agendar cita para ID: $id")
        }

        composable("${Routes.CITA}/{id}") { backStackEntry ->

            val id = backStackEntry.arguments?.getString("id") ?: ""

            CrearCitaScreen(
                navController = navController,
                id = id
            )
        }


        // 🗺 MAPA
        composable(Routes.MAPA) {
            Text("Pantalla Mapa")
        }

        // 👤 PERFIL





        composable(Routes.PERFIL) {
            PerfilScreen(
                onNavigateToLogin = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.PERFIL) { inclusive = true }
                    }
                },
                onNavigateToEditarPerfil = {
                    navController.navigate(Routes.EDITAR_PERFIL)
                }
            )
        }

        // ✏️ EDITAR PERFIL
        composable(Routes.EDITAR_PERFIL) {
            EditarPerfilScreen(navController)
        }
    }
}