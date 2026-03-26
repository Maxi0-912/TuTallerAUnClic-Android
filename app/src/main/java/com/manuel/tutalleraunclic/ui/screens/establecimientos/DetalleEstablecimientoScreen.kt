package com.manuel.tutalleraunclic.ui.screens.establecimientos
import androidx.hilt.navigation.compose.hiltViewModel
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
import com.manuel.tutalleraunclic.ui.components.ServicioCard

@Composable
fun DetalleEstablecimientoScreen(
    id: String,
    viewModel: ServiciosViewModel = hiltViewModel()
) {

    val establecimientoId = id.toIntOrNull() ?: return

    val servicios by viewModel.servicios.collectAsState()

    LaunchedEffect(establecimientoId) {
        viewModel.cargarServicios(establecimientoId)
    }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

        Text(
            text = "Detalle del establecimiento: $id",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Servicios disponibles",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        servicios.forEach { servicio ->

            Text(
                text = servicio.nombre, // 👈 asegúrate que existe este campo
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}