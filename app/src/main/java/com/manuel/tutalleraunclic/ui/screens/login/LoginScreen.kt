package com.manuel.tutalleraunclic.ui.screens.login

import com.manuel.tutalleraunclic.ui.components.LogoApp
import com.manuel.tutalleraunclic.core.navigation.Routes
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.platform.LocalContext

import com.manuel.tutalleraunclic.viewmodel.LoginViewModel
import com.manuel.tutalleraunclic.data.local.TokenManager
import com.manuel.tutalleraunclic.data.model.response.LoginResponse

@Composable
fun LoginScreen(navController: NavController) {

    val context = LocalContext.current

    val viewModel: LoginViewModel = viewModel()

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loginResult by viewModel.loginResult.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {

        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(24.dp))

        TextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                viewModel.login(username, password)
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Ingresar")

        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {

                navController.navigate(Routes.REGISTER)

            }
        ) {

            Text("Crear cuenta")

        }

        loginResult?.let {

            LaunchedEffect(it) {

                val tokenManager = TokenManager(context)

                // guardar token
                tokenManager.saveToken(it.access)

                navController.navigate(Routes.ESTABLECIMIENTOS) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
        }
    }

}