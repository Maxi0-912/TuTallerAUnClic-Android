package com.manuel.tutalleraunclic.ui.screens.citas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel

import com.manuel.tutalleraunclic.viewmodel.CitasViewModel


@Composable
fun CrearCitaScreen(

    navController: NavController,
    establecimientoId: Int,
    servicioId: Int,
    viewModel: CitasViewModel = viewModel()

) {

    var fecha by remember { mutableStateOf("") }
    var vehiculo by remember { mutableStateOf("") }

    Column(

        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)

    ) {

        Text(
            text = "Agendar cita",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(

            value = vehiculo,
            onValueChange = { vehiculo = it },
            label = { Text("Placa del vehículo") }

        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(

            value = fecha,
            onValueChange = { fecha = it },
            label = { Text("Fecha (YYYY-MM-DD)") }

        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(

            onClick = {

                viewModel.crearCita(

                    establecimiento = establecimientoId,
                    agenda = 1,
                    vehiculo = vehiculo,
                    servicio = servicioId,
                    fecha = fecha

                )

                navController.popBackStack()

            }

        ) {

            Text("Confirmar cita")

        }

    }

}