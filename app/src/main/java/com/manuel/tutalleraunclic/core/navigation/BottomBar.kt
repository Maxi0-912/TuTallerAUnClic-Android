package com.manuel.tutalleraunclic.core.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*

@Composable
fun BottomBar(navController: NavController) {

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

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