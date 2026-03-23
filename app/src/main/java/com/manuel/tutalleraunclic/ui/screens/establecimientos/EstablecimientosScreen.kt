package com.manuel.tutalleraunclic.ui.screens.establecimientos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import androidx.navigation.NavController
import com.manuel.tutalleraunclic.core.navigation.Routes
import com.manuel.tutalleraunclic.ui.components.EstablecimientoCard
import com.manuel.tutalleraunclic.viewmodel.EstablecimientosViewModel

data class EstablecimientoUI(
    val id: Int,
    val nombre: String,
    val direccion: String,
    val calificacion: String,
    val imagen: String
)

@Composable
fun EstablecimientosScreen(navController: NavController) {

    val viewModel: EstablecimientosViewModel =
        androidx.lifecycle.viewmodel.compose.viewModel()

    val lista = viewModel.lista

    LaunchedEffect(Unit) {
        viewModel.cargarEstablecimientos()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Establecimientos")

        Spacer(modifier = Modifier.height(16.dp))

        if (lista.isEmpty()) {

            // 🔥 ESTADO VACÍO
            Text("No hay establecimientos disponibles")

        } else {

            LazyColumn {

                items(lista) { establecimiento ->

                    EstablecimientoCard(
                        nombre = establecimiento.nombre,
                        direccion = establecimiento.direccion,
                        calificacion = "N/A",
                        imagen = "https://picsum.photos/400",
                        onClick = {
                            navController.navigate(
                                Routes.detalleEstablecimiento(establecimiento.id)
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }

}