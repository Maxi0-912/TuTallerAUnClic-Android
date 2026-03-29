package com.manuel.tutalleraunclic.ui.screens.establecimientos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.manuel.tutalleraunclic.viewmodel.ServiciosViewModel
import com.manuel.tutalleraunclic.ui.components.ServicioCard
import com.manuel.tutalleraunclic.core.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleEstablecimientoScreen(
    id: String,
    navController: NavController,
    viewModel: ServiciosViewModel = hiltViewModel()
) {

    val establecimientoId = id.toIntOrNull() ?: return
    val servicios by viewModel.servicios.collectAsState()

    LaunchedEffect(establecimientoId) {
        viewModel.cargarServicios(establecimientoId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Establecimiento #$id") }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {

            item {
                Text(
                    text = "Servicios disponibles",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(12.dp))
            }

            items(servicios) { servicio ->

                ServicioCard(
                    servicio = servicio,
                    onClick = {
                        // 🔥 NAVEGACIÓN PRO (SEGURA)
                        navController.navigate(
                            Routes.cita(
                                establecimientoId = establecimientoId,
                                servicioId = servicio.id
                            )
                        )
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}