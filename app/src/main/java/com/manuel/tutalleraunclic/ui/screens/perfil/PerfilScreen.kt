package com.manuel.tutalleraunclic.ui.screens.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PerfilScreen() {

    var mostrarDialogo by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Mi Perfil",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Nombre: Usuario")
        Text("Email: usuario@email.com")

        Spacer(modifier = Modifier.height(30.dp))

        Button(
            onClick = { /* cerrar sesión */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = { mostrarDialogo = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Eliminar cuenta")
        }

    }

    if (mostrarDialogo) {

        AlertDialog(
            onDismissRequest = { mostrarDialogo = false },

            title = {
                Text("Eliminar cuenta")
            },

            text = {
                Text("¿Seguro que deseas eliminar tu cuenta? Esta acción no se puede deshacer.")
            },

            confirmButton = {

                TextButton(
                    onClick = {

                        mostrarDialogo = false

                        // Aquí llamas tu API para eliminar cuenta

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