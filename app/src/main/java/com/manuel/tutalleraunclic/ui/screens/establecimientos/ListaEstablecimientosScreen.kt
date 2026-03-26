package com.manuel.tutalleraunclic.ui.screens.establecimientos

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.navigation.NavController


@Composable
fun ListaEstablecimientosScreen(navController: NavController) {

    val establecimientos = listOf(
        "1", "2", "3"
    )

    Column {
        establecimientos.forEach { id ->

            Button(
                onClick = {
                    navController.navigate("detalle_establecimiento/$id")
                }
            ) {
                Text("Ir al establecimiento $id")
            }
        }
    }
}