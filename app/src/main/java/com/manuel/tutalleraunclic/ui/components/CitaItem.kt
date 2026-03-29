package com.manuel.tutalleraunclic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.manuel.tutalleraunclic.data.model.entity.Cita

@Composable
fun CitaItem(cita: Cita) {

    val estadoColor = when (cita.estado) {
        "pendiente" -> Color(0xFFFFA000)
        "confirmada" -> Color(0xFF2E7D32)
        "cancelada" -> Color.Red
        else -> Color.Gray
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "📅 ${cita.fecha} - ${cita.hora}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text("🛠 Servicio: ${cita.servicio}")

            Spacer(modifier = Modifier.height(4.dp))

            Text("🏢 Establecimiento: ${cita.establecimiento}")

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = cita.estado.uppercase(),
                color = estadoColor,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}