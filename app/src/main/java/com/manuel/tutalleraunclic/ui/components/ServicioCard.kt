package com.manuel.tutalleraunclic.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.manuel.tutalleraunclic.data.model.entity.Servicio

@OptIn(ExperimentalMaterial3Api::class) // ✅ VA AQUÍ

@Composable
fun ServicioCard(
    servicio: Servicio,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = servicio.nombre,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Precio: $${servicio.tipo_servicio.precio}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}