package com.manuel.tutalleraunclic.core.navigation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument

import com.manuel.tutalleraunclic.ui.screens.login.ForgotPasswordScreen
import com.manuel.tutalleraunclic.ui.screens.login.LoginScreen
import com.manuel.tutalleraunclic.ui.screens.login.SeleccionarRolScreen
import com.manuel.tutalleraunclic.ui.screens.register.RegisterScreen
import com.manuel.tutalleraunclic.ui.screens.establecimientos.*
import com.manuel.tutalleraunclic.ui.screens.citas.*
import com.manuel.tutalleraunclic.ui.screens.perfil.PerfilEmpresaScreen
import com.manuel.tutalleraunclic.ui.screens.perfil.PerfilScreen
import com.manuel.tutalleraunclic.ui.screens.perfil.EditarPerfilScreen
import com.manuel.tutalleraunclic.ui.screens.mapa.MapScreen
import com.manuel.tutalleraunclic.ui.screens.notificaciones.NotificacionesScreen
import com.manuel.tutalleraunclic.ui.screens.notificaciones.NotificacionesEmpresaScreen
import com.manuel.tutalleraunclic.ui.screens.resena.ResenaScreen
import com.manuel.tutalleraunclic.ui.screens.anuncios.MisAnunciosScreen
import com.manuel.tutalleraunclic.ui.screens.anuncios.AnuncioFormScreen
import com.manuel.tutalleraunclic.ui.screens.dashboard.DashboardEmpresaScreen
import com.manuel.tutalleraunclic.viewmodel.NotificacionesViewModel
import com.manuel.tutalleraunclic.viewmodel.RolViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Store

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
            currentRoute.startsWith(Routes.FORGOT_PASSWORD) -> false
            currentRoute.startsWith(Routes.SELECCIONAR_ROL) -> false
            currentRoute.startsWith(Routes.CITA) -> false
            currentRoute.startsWith(Routes.DETALLE)  -> false
            currentRoute.startsWith(Routes.TALLER)   -> false
            currentRoute.startsWith(Routes.LAVADERO)  -> false
            currentRoute.startsWith("editar_cita") -> false
            currentRoute.startsWith(Routes.EDITAR_PERFIL) -> false
            currentRoute.startsWith("resena") -> false
            currentRoute.startsWith(Routes.MIS_ANUNCIOS) -> false
            currentRoute.startsWith(Routes.ANUNCIO_CREAR) -> false
            currentRoute.startsWith(Routes.ANUNCIO_EDITAR) -> false
            currentRoute.startsWith(Routes.MIS_ESTABLECIMIENTOS) -> false
            currentRoute.startsWith(Routes.ESTABLECIMIENTO_EMP_CREAR) -> false
            currentRoute.startsWith(Routes.ESTABLECIMIENTO_EMP_EDITAR) -> false
            currentRoute.startsWith(Routes.MIS_SERVICIOS) -> false
            currentRoute.startsWith(Routes.SERVICIO_EMP_CREAR) -> false
            currentRoute.startsWith(Routes.SERVICIO_EMP_EDITAR) -> false
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
                    },
                    onForgotPasswordClick = {
                        navController.navigate(Routes.FORGOT_PASSWORD)
                    }
                )
            }

            composable(Routes.FORGOT_PASSWORD) {
                ForgotPasswordScreen(onBack = { navController.popBackStack() })
            }

            composable(Routes.SELECCIONAR_ROL) {
                SeleccionarRolScreen(
                    onRolSeleccionado = { rol ->
                        when (rol.lowercase()) {
                            "empresa" -> navController.navigate(Routes.EMPRESA_HOME) {
                                popUpTo(Routes.SELECCIONAR_ROL) { inclusive = true }
                                launchSingleTop = true
                            }
                            else -> navController.navigate(Routes.ESTABLECIMIENTOS) {
                                popUpTo(Routes.SELECCIONAR_ROL) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
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
                EstablecimientosScreen(navController = navController)
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

            // 🏢 EMPRESA HOME (Dashboard)
            composable(Routes.EMPRESA_HOME) {
                DashboardEmpresaScreen(
                    onVerCitas = {
                        navController.navigate(Routes.EMPRESA_CITAS) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    onRegistrarEstablecimiento = {
                        navController.navigate(Routes.EMPRESA_ESTABLECIMIENTO) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }

            composable(Routes.EMPRESA_CITAS) {
                CitasEmpresaScreen()
            }

            composable(Routes.EMPRESA_ESTABLECIMIENTO) {
                EmpresaEstablecimientoHub(
                    onMisAnuncios = { navController.navigate(Routes.MIS_ANUNCIOS) },
                    onMisEstablecimientos = { navController.navigate(Routes.MIS_ESTABLECIMIENTOS) },
                    onMisServicios = { navController.navigate(Routes.MIS_SERVICIOS) }
                )
            }

            // 📢 ANUNCIOS (empresa)
            composable(Routes.MIS_ANUNCIOS) {
                MisAnunciosScreen(
                    onBack = { navController.popBackStack() },
                    onCrear = { navController.navigate(Routes.ANUNCIO_CREAR) },
                    onEditar = { id -> navController.navigate(Routes.anuncioEditar(id)) }
                )
            }

            composable(Routes.ANUNCIO_CREAR) {
                AnuncioFormScreen(
                    anuncioId = null,
                    onBack = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.ANUNCIO_EDITAR_ARG,
                arguments = listOf(navArgument("anuncioId") { type = NavType.IntType })
            ) { backStackEntry ->
                val anuncioId = backStackEntry.arguments?.getInt("anuncioId") ?: 0
                AnuncioFormScreen(
                    anuncioId = anuncioId,
                    onBack = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
                )
            }

            // 🏢 MIS ESTABLECIMIENTOS (empresa)
            composable(Routes.MIS_ESTABLECIMIENTOS) {
                MisEstablecimientosScreen(
                    onBack = { navController.popBackStack() },
                    onCrear = { navController.navigate(Routes.ESTABLECIMIENTO_EMP_CREAR) },
                    onEditar = { id -> navController.navigate(Routes.establecimientoEmpEditar(id)) }
                )
            }

            composable(Routes.ESTABLECIMIENTO_EMP_CREAR) {
                EstablecimientoFormScreen(
                    establecimientoId = null,
                    onBack = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.ESTABLECIMIENTO_EMP_EDITAR_ARG,
                arguments = listOf(navArgument("establecimientoId") { type = NavType.IntType })
            ) { backStackEntry ->
                val establecimientoId = backStackEntry.arguments?.getInt("establecimientoId") ?: 0
                EstablecimientoFormScreen(
                    establecimientoId = establecimientoId,
                    onBack = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
                )
            }

            // 🔧 MIS SERVICIOS (empresa)
            composable(Routes.MIS_SERVICIOS) {
                MisServiciosScreen(
                    onBack = { navController.popBackStack() },
                    onCrear = { navController.navigate(Routes.SERVICIO_EMP_CREAR) },
                    onEditar = { id -> navController.navigate(Routes.servicioEmpEditar(id)) }
                )
            }

            composable(Routes.SERVICIO_EMP_CREAR) {
                ServicioFormScreen(
                    servicioId = null,
                    onBack = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
                )
            }

            composable(
                route = Routes.SERVICIO_EMP_EDITAR_ARG,
                arguments = listOf(navArgument("servicioId") { type = NavType.IntType })
            ) { backStackEntry ->
                val servicioId = backStackEntry.arguments?.getInt("servicioId") ?: 0
                ServicioFormScreen(
                    servicioId = servicioId,
                    onBack = { navController.popBackStack() },
                    onSuccess = { navController.popBackStack() }
                )
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

            // 🔔 ALERTAS (empresa)
            composable(Routes.EMPRESA_ALERTAS) {
                NotificacionesEmpresaScreen(viewModel = notifViewModel)
            }

            // 👤 PERFIL
            composable(Routes.PERFIL) {
                if (rolActual?.lowercase() == "empresa") {
                    PerfilEmpresaScreen(
                        onNavigateToLogin = {
                            navController.navigate(Routes.LOGIN) {
                                popUpTo(0) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                } else {
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
            }

            composable(Routes.EDITAR_PERFIL) {
                EditarPerfilScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

/**
 * Hub de la pestaña "Mi Taller" del rol empresa. Punto de entrada a las secciones
 * de gestión del establecimiento; hoy solo enlaza a Mis anuncios/Promociones,
 * pero deja espacio para agregar servicios/fotos/perfil del taller más adelante.
 */
@Composable
private fun EmpresaEstablecimientoHub(
    onMisAnuncios: () -> Unit,
    onMisEstablecimientos: () -> Unit,
    onMisServicios: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            "Mi Taller",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        HubEntry(
            icon = Icons.Default.Store,
            titulo = "Mis establecimientos",
            subtitulo = "Registra y edita tus talleres y lavaderos",
            onClick = onMisEstablecimientos
        )
        Spacer(Modifier.height(12.dp))
        HubEntry(
            icon = Icons.Default.Build,
            titulo = "Servicios",
            subtitulo = "Administra los servicios que ofrece cada establecimiento",
            onClick = onMisServicios
        )
        Spacer(Modifier.height(12.dp))
        HubEntry(
            icon = Icons.Default.Campaign,
            titulo = "Mis anuncios / Promociones",
            subtitulo = "Crea y gestiona los anuncios de tu establecimiento",
            onClick = onMisAnuncios
        )
    }
}

@Composable
private fun HubEntry(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    titulo: String,
    subtitulo: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null)
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(titulo, style = MaterialTheme.typography.titleMedium)
                Text(
                    subtitulo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null)
        }
    }
}
