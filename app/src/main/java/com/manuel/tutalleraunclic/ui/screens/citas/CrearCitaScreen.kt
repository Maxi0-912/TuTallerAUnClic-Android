package com.manuel.tutalleraunclic.ui.screens.citas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CrearCitaScreen(
    navController: NavController,
    id: String
) {

    var fecha by remember { mutableStateOf("Seleccionar fecha") }
    var horaSeleccionada by remember { mutableStateOf("") }

    val horas = listOf(
        "08:00", "09:00", "10:00",
        "11:00", "13:00", "14:00",
        "15:00", "16:00"
    )

    val servicios = listOf(
        "Cambio de aceite",
        "Lavado",
        "Diagnóstico",
        "Alineación"
    )

    val seleccionados = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "Agendar Cita",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 📅 FECHA
        Button(onClick = {
            fecha = "2026-04-01" // luego conectamos DatePicker real
        }) {
            Text(fecha)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ⏰ HORAS
        Text("Selecciona una hora")

        LazyRow {
            items(horas) { hora ->

                FilterChip(
                    selected = horaSeleccionada == hora,
                    onClick = { horaSeleccionada = hora },
                    label = { Text(hora) },
                    modifier = Modifier.padding(end = 8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 🔧 SERVICIOS
        Text("Servicios")

        servicios.forEach { servicio ->

            Row(
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {

                Checkbox(
                    checked = seleccionados.contains(servicio),
                    onCheckedChange = {
                        if (it) seleccionados.add(servicio)
                        else seleccionados.remove(servicio)
                    }
                )

                Text(servicio)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🔥 BOTÓN FINAL
        Button(
            onClick = {
                println("Cita creada: $id - $fecha - $horaSeleccionada - $seleccionados")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Confirmar Cita")
        }
    }
}