package com.manuel.tutalleraunclic.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavController
import androidx.navigation.compose.composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.manuel.tutalleraunclic.ui.screens.register.RegisterScreen
import com.manuel.tutalleraunclic.ui.screens.login.LoginScreen
import com.manuel.tutalleraunclic.viewmodel.LoginViewModel
import  com.manuel.tutalleraunclic.viewmodel.RegisterViewModel

fun NavGraphBuilder.authGraph(navController: NavController) {

    // 🔐 LOGIN
    composable(Routes.LOGIN) {

        val viewModel: LoginViewModel = hiltViewModel()

        LoginScreen(
            navController = navController,
            viewModel = viewModel
        )
    }

    // 📝 REGISTER
    composable(Routes.REGISTER) {

        val viewModel: RegisterViewModel = hiltViewModel()

        RegisterScreen(
            onNavigateToHome = {
                navController.navigate(Routes.ESTABLECIMIENTOS) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                    launchSingleTop = true
                }
            },
            onNavigateToEmpresa = {
                navController.navigate(Routes.EMPRESA_HOME) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                    launchSingleTop = true
                }
            },
            onNavigateToLogin = {
                navController.popBackStack()
            },
            viewModel = viewModel
        )
    }
}