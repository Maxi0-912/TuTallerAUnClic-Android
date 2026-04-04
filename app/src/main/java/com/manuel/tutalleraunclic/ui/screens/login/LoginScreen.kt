package com.manuel.tutalleraunclic.ui.screens.login

import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.livedata.observeAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.graphics.Color

import com.manuel.tutalleraunclic.core.navigation.Routes
import com.manuel.tutalleraunclic.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    navController: NavController
) {

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val loading by viewModel.loading.observeAsState(false)
    val error by viewModel.error.observeAsState()
    val success by viewModel.success.observeAsState(false)

    var alreadyNavigated by remember { mutableStateOf(false) }

    // 🚀 Navegación cuando login es exitoso
    LaunchedEffect(success) {
        if (success) {
            navController.navigate(Routes.ESTABLECIMIENTOS) {
                popUpTo(Routes.LOGIN) { inclusive = true }
                launchSingleTop = true
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Usuario") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                viewModel.login(username, password)
            },
            enabled = !loading
        ) {
            Text(if (loading) "Cargando..." else "Iniciar sesión")
        }

        error?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = it, color = Color.Red)
        }

        TextButton(
            onClick = {
                navController.navigate(Routes.REGISTER)
            }
        ) {
            Text("¿No tienes cuenta? Regístrate")
        }
    }
}