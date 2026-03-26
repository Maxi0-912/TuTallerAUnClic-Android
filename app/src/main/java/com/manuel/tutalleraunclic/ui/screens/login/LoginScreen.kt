package com.manuel.tutalleraunclic.ui.screens.login

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel

import com.manuel.tutalleraunclic.core.navigation.Routes
import com.manuel.tutalleraunclic.viewmodel.LoginViewModel
import com.manuel.tutalleraunclic.data.local.TokenManager

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {

    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginResult by viewModel.loginResult.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text("Iniciar Sesión")

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") }
        )

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") }
        )

        Button(onClick = {
            viewModel.login(username, password)
        }) {
            Text("Ingresar")
        }

        TextButton(onClick = {
            navController.navigate(Routes.REGISTER)
        }) {
            Text("Crear cuenta")
        }

        loginResult?.let {
            LaunchedEffect(it) {

                val tokenManager = TokenManager(context.applicationContext)
                tokenManager.saveToken(it.access)

                navController.navigate(Routes.ESTABLECIMIENTOS) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
        }
    }
}