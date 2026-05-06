package com.manuel.tutalleraunclic.core.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(
    navController: NavController,
    notifUnreadCount: Int = 0,
    rolNombre: String? = null
) {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    if (rolNombre?.lowercase()?.trim() == "empresa") {
        // ── BottomBar EMPRESA ─────────────────────────────────────────────────
        NavigationBar {

            NavigationBarItem(
                selected = currentRoute == Routes.EMPRESA_HOME,
                onClick = {
                    navController.navigate(Routes.EMPRESA_HOME) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                label = { Text("Dashboard") }
            )

            NavigationBarItem(
                selected = currentRoute == Routes.EMPRESA_CITAS,
                onClick = {
                    navController.navigate(Routes.EMPRESA_CITAS) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(Icons.Default.Event, contentDescription = "Citas") },
                label = { Text("Citas") }
            )

            NavigationBarItem(
                selected = currentRoute == Routes.EMPRESA_ESTABLECIMIENTO,
                onClick = {
                    navController.navigate(Routes.EMPRESA_ESTABLECIMIENTO) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(Icons.Default.Store, contentDescription = "Establecimiento") },
                label = { Text("Mi Taller") }
            )

            NavigationBarItem(
                selected = currentRoute == Routes.NOTIFICACIONES,
                onClick = {
                    navController.navigate(Routes.NOTIFICACIONES) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = {
                    BadgedBox(
                        badge = {
                            if (notifUnreadCount > 0) {
                                Badge {
                                    Text(if (notifUnreadCount > 9) "9+" else "$notifUnreadCount")
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "Alertas")
                    }
                },
                label = { Text("Alertas") }
            )

            NavigationBarItem(
                selected = currentRoute == Routes.PERFIL,
                onClick = {
                    navController.navigate(Routes.PERFIL) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                label = { Text("Perfil") }
            )
        }
    } else {
        // ── BottomBar CLIENTE (sin cambios) ───────────────────────────────────
        NavigationBar {

            NavigationBarItem(
                selected = currentRoute == Routes.ESTABLECIMIENTOS,
                onClick = {
                    navController.navigate(Routes.ESTABLECIMIENTOS) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") },
                label = { Text("Inicio") }
            )

            NavigationBarItem(
                selected = currentRoute == Routes.MIS_CITAS,
                onClick = {
                    navController.navigate(Routes.MIS_CITAS) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(Icons.Default.Event, contentDescription = "Citas") },
                label = { Text("Citas") }
            )

            NavigationBarItem(
                selected = currentRoute == Routes.MAPA,
                onClick = {
                    navController.navigate(Routes.MAPA) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(Icons.Default.Map, contentDescription = "Mapa") },
                label = { Text("Mapa") }
            )

            NavigationBarItem(
                selected = currentRoute == Routes.NOTIFICACIONES,
                onClick = {
                    navController.navigate(Routes.NOTIFICACIONES) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = {
                    BadgedBox(
                        badge = {
                            if (notifUnreadCount > 0) {
                                Badge {
                                    Text(if (notifUnreadCount > 9) "9+" else "$notifUnreadCount")
                                }
                            }
                        }
                    ) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notificaciones")
                    }
                },
                label = { Text("Alertas") }
            )

            NavigationBarItem(
                selected = currentRoute == Routes.PERFIL,
                onClick = {
                    navController.navigate(Routes.PERFIL) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") },
                label = { Text("Perfil") }
            )
        }
    }
}
