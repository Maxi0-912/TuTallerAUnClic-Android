package com.manuel.tutalleraunclic.core.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.manuel.tutalleraunclic.ui.screens.login.LoginScreen
import com.manuel.tutalleraunclic.ui.screens.register.RegisterScreen

fun NavGraphBuilder.authGraph(navController: NavController) {

    composable(Routes.LOGIN) {
        LoginScreen(navController)
    }

    composable(Routes.REGISTER) {
        RegisterScreen(navController)

    }



}