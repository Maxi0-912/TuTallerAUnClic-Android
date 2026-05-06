package com.manuel.tutalleraunclic.ui.screens.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.Alignment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.saveable.rememberSaveable
import com.manuel.tutalleraunclic.core.navigation.Routes
import com.manuel.tutalleraunclic.viewmodel.LoginViewModel
import com.manuel.tutalleraunclic.viewmodel.RolViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    rolViewModel: RolViewModel? = null,
    onNavigateToRegister: () -> Unit = {},
    viewModel: LoginViewModel = hiltViewModel()
) {
    val loading   by viewModel.loading.collectAsStateWithLifecycle()
    val success   by viewModel.success.collectAsStateWithLifecycle()
    val rolNombre by viewModel.rolNombre.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var username by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }

    // Show error messages
    LaunchedEffect(Unit) {
        viewModel.error.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Navigate by role after a successful login
    LaunchedEffect(success, rolNombre) {
        if (success) {
            rolViewModel?.refresh()
            kotlinx.coroutines.delay(100)
            when (rolNombre?.lowercase()?.trim()) {
                "empresa" -> navController.navigate(Routes.EMPRESA_HOME) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                    launchSingleTop = true
                }
                else -> navController.navigate(Routes.ESTABLECIMIENTOS) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                    launchSingleTop = true
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Text(
                    text = "Iniciar sesión",
                    style = MaterialTheme.typography.headlineMedium
                )

                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Usuario") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        autoCorrect = false
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        autoCorrect = false
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {
                        if (username.isBlank() || password.isBlank()) {
                            // Input validation feedback
                        } else {
                            viewModel.login(username.trim(), password)
                        }
                    },
                    enabled = !loading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Iniciar sesión")
                    }
                }

                TextButton(
                    onClick = { navController.navigate(Routes.REGISTER) },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("¿No tienes cuenta? Regístrate")
                }
            }
        }
    }
}
