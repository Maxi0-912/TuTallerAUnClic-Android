package com.manuel.tutalleraunclic.ui.screens.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.manuel.tutalleraunclic.ui.components.BottomNavItem
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.manuel.tutalleraunclic.core.navigation.BottomBar

import com.manuel.tutalleraunclic.core.navigation.Routes
import com.manuel.tutalleraunclic.core.navigation.mainGraph


// =========================
// 🚀 MAIN SCREEN
// =========================
@Composable
fun MainScreen() {

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = Routes.ESTABLECIMIENTOS,
            modifier = Modifier.padding(padding)
        ) {
            mainGraph(navController)
        }
    }
}