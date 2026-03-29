package com.manuel.tutalleraunclic.ui.screens.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*

import com.manuel.tutalleraunclic.core.navigation.Routes
import com.manuel.tutalleraunclic.core.navigation.mainGraph

@Composable
fun MainScreen() {

    val navController = rememberNavController()

    // 🔥 Detectar ruta actual
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {

                NavigationBarItem(
                    selected = currentRoute == Routes.ESTABLECIMIENTOS,
                    onClick = {
                        navController.navigate(Routes.ESTABLECIMIENTOS) {
                            popUpTo(Routes.ESTABLECIMIENTOS)
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(Icons.Default.Home, contentDescription = "Inicio")
                    },
                    label = { Text("Inicio") }
                )

                NavigationBarItem(
                    selected = currentRoute == Routes.MAPA,
                    onClick = {
                        navController.navigate(Routes.MAPA) {
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(Icons.Default.LocationOn, contentDescription = "Mapa")
                    },
                    label = { Text("Mapa") }
                )

                NavigationBarItem(
                    selected = currentRoute == Routes.MIS_CITAS,
                    onClick = {
                        navController.navigate(Routes.MIS_CITAS) {
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Citas")
                    },
                    label = { Text("Citas") }
                )

                NavigationBarItem(
                    selected = currentRoute == Routes.PERFIL,
                    onClick = {
                        navController.navigate(Routes.PERFIL) {
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(Icons.Default.Person, contentDescription = "Perfil")
                    },
                    label = { Text("Perfil") }
                )
            }
        }

    ) { padding ->

        Box(
            modifier = Modifier.padding(padding)
        ) {

            NavHost(
                navController = navController,
                startDestination = Routes.ESTABLECIMIENTOS
            ) {
                mainGraph(navController)
            }
        }
    }
}