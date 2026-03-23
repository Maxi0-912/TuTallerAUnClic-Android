package com.manuel.tutalleraunclic.ui.screens.establecimientos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

import com.manuel.tutalleraunclic.viewmodel.ServiciosViewModel
import com.manuel.tutalleraunclic.data.model.entity.Servicio
import com.manuel.tutalleraunclic.ui.components.ServicioCard

@Composable
fun DetalleEstablecimientoScreen(
    establecimientoId: Int,
    navController: NavController,
    viewModel: ServiciosViewModel = viewModel()
) {

    val servicios by viewModel.servicios.observeAsState(emptyList())

    LaunchedEffect(establecimientoId) {
        viewModel.cargarServicios(establecimientoId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Servicios disponibles",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {

            items(servicios) { servicio ->

                ServicioCard(servicio = servicio)

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {

                        val servicioId = servicio.id ?: return@Button

                        navController.navigate(
                            "crear_cita/$establecimientoId/$servicioId"
                        )
                    }
                ) {
                    Text("Agendar cita")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}