package com.manuel.tutalleraunclic.core.navigation

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person

@Composable
fun BottomBar(navController: NavController) {

    NavigationBar {

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Routes.ESTABLECIMIENTOS) },
            icon = { Icon(Icons.Default.Home, contentDescription = "Inicio") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Routes.MIS_CITAS) },
            icon = { Icon(Icons.Default.Event, contentDescription = "Citas") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Routes.MAPA) },
            icon = { Icon(Icons.Default.Map, contentDescription = "Mapa") }
        )

        NavigationBarItem(
            selected = false,
            onClick = { navController.navigate(Routes.PERFIL) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Perfil") }
        )
    }
}