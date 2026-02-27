package com.manuel.tutalleraunclic.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import com.manuel.tutalleraunclic.ui.screens.home.HomeScreen
import com.manuel.tutalleraunclic.ui.screens.login.LoginScreen
import com.manuel.tutalleraunclic.utils.TokenManager

@Composable
fun AppNavGraph(navController: NavHostController) {

    val context = LocalContext.current
    val startDestination =
        if (TokenManager.getToken(context) != null) "home"
        else "login"

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {

        composable("login") {
            LoginScreen(navController)
        }

        composable("home") {
            HomeScreen(navController)
        }
    }
}