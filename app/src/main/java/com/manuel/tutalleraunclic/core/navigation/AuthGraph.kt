package com.manuel.tutalleraunclic.core.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavController
import androidx.navigation.compose.composable
import androidx.hilt.navigation.compose.hiltViewModel
import com.manuel.tutalleraunclic.ui.screens.register.RegisterScreen
import com.manuel.tutalleraunclic.ui.screens.login.LoginScreen
import com.manuel.tutalleraunclic.viewmodel.LoginViewModel

fun NavGraphBuilder.authGraph(navController: NavController) {

    // 🔐 LOGIN
    composable(Routes.LOGIN) {

        val viewModel: LoginViewModel = hiltViewModel()

        LoginScreen(
            navController = navController,
            viewModel = viewModel
        )
    }

    // 📝 REGISTER (ejemplo)
    composable(Routes.REGISTER) {
        RegisterScreen(navController = navController)
    }
}