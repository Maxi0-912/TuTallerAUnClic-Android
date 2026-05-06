package com.manuel.tutalleraunclic.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

import com.manuel.tutalleraunclic.ui.screens.login.LoginScreen
import com.manuel.tutalleraunclic.ui.screens.register.RegisterScreen
import com.manuel.tutalleraunclic.ui.screens.establecimientos.*
import com.manuel.tutalleraunclic.ui.screens.citas.*
import com.manuel.tutalleraunclic.ui.screens.perfil.PerfilScreen
import com.manuel.tutalleraunclic.ui.screens.perfil.EditarPerfilScreen
import com.manuel.tutalleraunclic.ui.screens.mapa.MapScreen
import com.manuel.tutalleraunclic.ui.screens.notificaciones.NotificacionesScreen
import com.manuel.tutalleraunclic.ui.screens.resena.ResenaScreen
import com.manuel.tutalleraunclic.viewmodel.NotificacionesViewModel
import com.manuel.tutalleraunclic.viewmodel.RolViewModel

/**
 * @param isLoggedIn  Whether a valid session token is already stored.
 * @param startRoute  The initial destination. Computed by MainActivity from
 *                    the saved role so empresa/cliente start on the right screen
 *                    even after an app restart.
 */
@Composable
fun AppRoot(
    isLoggedIn: Boolean,
    startRoute: String = if (isLoggedIn) Routes.ESTABLECIMIENTOS else Routes.LOGIN
) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    // RolViewModel scoped to Activity — shared instance passed down to LoginScreen
    val rolViewModel: RolViewModel = hiltViewModel()
    val rolActual by rolViewModel.rolNombre.collectAsStateWithLifecycle()

    val notifViewModel: NotificacionesViewModel = hiltViewModel()
    val unreadCount by notifViewModel.unreadCount.collectAsState()

    val showBottomBar = remember(currentRoute) {
        when {
            currentRoute == null -> false
            currentRoute.startsWith(Routes.LOGIN) -> false
            currentRoute.startsWith(Routes.REGISTER) -> false
            currentRoute.startsWith(Routes.CITA) -> false
            currentRoute.startsWith(Routes.DETALLE)  -> false
            currentRoute.startsWith(Routes.TALLER)   -> false
            currentRoute.startsWith(Routes.LAVADERO)  -> false
            currentRoute.startsWith("editar_cita") -> false
            currentRoute.startsWith(Routes.EDITAR_PERFIL) -> false
            currentRoute.startsWith("resena") -> false
            else -> true
        }
    }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomBar(navController, unreadCount, rolActual)
            }
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = Modifier.padding(padding)
        ) {

            // 🔐 AUTH
            composable(Routes.LOGIN) {
                LoginScreen(
                    navController = navController,
                    rolViewModel = rolViewModel,
                    onNavigateToRegister = {
                        navController.navigate(Routes.REGISTER)
                    }
                )
            }

            composable(Routes.REGISTER) {
                RegisterScreen(
                    onNavigateToHome = {
                        navController.navigate(Routes.ESTABLECIMIENTOS) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToEmpresa = {
                        navController.navigate(Routes.EMPRESA_HOME) {
                            popUpTo(Routes.REGISTER) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }

            // 🏢 ESTABLECIMIENTOS
            composable(Routes.ESTABLECIMIENTOS) {
                ListaEstablecimientosScreen(navController)
            }

            composable(
                route = Routes.DETALLE_ARG,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val establecimientoId = backStackEntry.arguments?.getInt("id") ?: 0
                DetalleEstablecimientoScreen(
                    establecimientoId = establecimientoId,
                    navController = navController
                )
            }

            composable(
                route = Routes.TALLER_ARG,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val establecimientoId = backStackEntry.arguments?.getInt("id") ?: 0
                DetalleEstablecimientoScreen(
                    establecimientoId = establecimientoId,
                    navController = navController
                )
            }

            composable(
                route = Routes.LAVADERO_ARG,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val establecimientoId = backStackEntry.arguments?.getInt("id") ?: 0
                DetalleEstablecimientoScreen(
                    establecimientoId = establecimientoId,
                    navController = navController
                )
            }

            // 📅 CITAS
            composable(
                route = Routes.CITA_ARG,
                arguments = listOf(
                    navArgument("establecimientoId") { type = NavType.IntType },
                    navArgument("servicioId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val establecimientoId = backStackEntry.arguments?.getInt("establecimientoId") ?: 0
                val servicioId = backStackEntry.arguments?.getInt("servicioId") ?: 0
                CrearCitaScreen(
                    establecimientoId = establecimientoId,
                    servicioId = servicioId,
                    onSuccess = { navController.popBackStack() },
                    onBack    = { navController.popBackStack() }
                )
            }

            composable(Routes.MIS_CITAS) {
                MisCitasScreen(navController = navController)
            }

            composable("editar_cita/{citaId}") { backStackEntry ->
                val citaId = backStackEntry.arguments?.getString("citaId")?.toInt() ?: 0
                EditarCitaScreen(
                    citaId    = citaId,
                    onSuccess = { navController.popBackStack() },
                    onBack    = { navController.popBackStack() }
                )
            }

            // ⭐ RESEÑA
            composable(
                route = Routes.RESENA_ARG,
                arguments = listOf(
                    navArgument("citaId") { type = NavType.IntType },
                    navArgument("establecimientoId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val citaId = backStackEntry.arguments?.getInt("citaId") ?: 0
                val establecimientoId = backStackEntry.arguments?.getInt("establecimientoId") ?: 0
                ResenaScreen(
                    citaId = citaId,
                    establecimientoId = establecimientoId,
                    onSuccess = { navController.popBackStack() },
                    onBack = { navController.popBackStack() }
                )
            }

            // 🏢 EMPRESA HOME
            composable(Routes.EMPRESA_HOME) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Panel de empresa - Próximamente", style = MaterialTheme.typography.titleMedium)
                }
            }

            // 🗺️ MAPA
            composable(Routes.MAPA) {
                MapScreen(navController = navController)
            }

            // 🔔 NOTIFICACIONES
            composable(Routes.NOTIFICACIONES) {
                NotificacionesScreen(
                    viewModel = notifViewModel,
                    onBack    = { navController.popBackStack() }
                )
            }

            // 👤 PERFIL
            composable(Routes.PERFIL) {
                PerfilScreen(
                    onNavigateToLogin = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    onNavigateToEditarPerfil = {
                        navController.navigate(Routes.EDITAR_PERFIL)
                    }
                )
            }

            composable(Routes.EDITAR_PERFIL) {
                EditarPerfilScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
