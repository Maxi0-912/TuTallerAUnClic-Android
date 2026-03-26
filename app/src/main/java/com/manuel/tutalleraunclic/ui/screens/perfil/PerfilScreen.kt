package com.manuel.tutalleraunclic.ui.screens.perfil

import com.manuel.tutalleraunclic.viewmodel.PerfilViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight

@Composable
fun PerfilScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToEditarPerfil: () -> Unit
) {

    val viewModel: PerfilViewModel = viewModel()
    val context = LocalContext.current

    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()

    var mostrarDialogo by remember { mutableStateOf(false) }

    // 🚀 navegación automática
    LaunchedEffect(success) {
        if (success) {
            onNavigateToLogin()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(20.dp))

        // 🖼 FOTO PERFIL
        AsyncImage(
            model = "https://i.pravatar.cc/300",
            contentDescription = "Foto perfil",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 👤 NOMBRE
        Text(
            text = "Usuario Demo",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            )
        )

        Spacer(modifier = Modifier.height(30.dp))

        // 🔘 BOTÓN EDITAR
        Button(
            onClick = { onNavigateToEditarPerfil() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Editar perfil")
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 🔘 CERRAR SESIÓN
        OutlinedButton(
            onClick = { onNavigateToLogin() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 🔴 ELIMINAR CUENTA
        Button(
            onClick = { mostrarDialogo = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Eliminar cuenta")
        }

        // 🔄 LOADING
        if (loading) {
            Spacer(modifier = Modifier.height(20.dp))
            CircularProgressIndicator()
        }

        // ❌ ERROR
        error?.let {
            Spacer(modifier = Modifier.height(20.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }

    // 🔥 DIÁLOGO CONFIRMACIÓN
    if (mostrarDialogo) {

        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },
            title = { Text("Eliminar cuenta") },
            text = {
                Text("¿Seguro que deseas eliminar tu cuenta? Esta acción no se puede deshacer.")
            },

            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarDialogo = false
                        viewModel.eliminarCuenta()
                    }
                ) {
                    Text("Eliminar")
                }
            },

            dismissButton = {
                TextButton(
                    onClick = { mostrarDialogo = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}