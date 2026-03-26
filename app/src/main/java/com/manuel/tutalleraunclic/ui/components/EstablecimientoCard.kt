package com.manuel.tutalleraunclic.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

import com.manuel.tutalleraunclic.data.model.EstablecimientoUI

@Composable
fun EstablecimientoCard(
    establecimiento: EstablecimientoUI,
    onClick: () -> Unit,
    onAgendarClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        onClick = onClick
    ) {

        Column {

            AsyncImage(
                model = establecimiento.imagen,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Column(modifier = Modifier.padding(12.dp)) {

                Text(
                    text = establecimiento.nombre,
                    style = MaterialTheme.typography.titleMedium
                )

                Text(
                    text = establecimiento.direccion,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "⭐ ${establecimiento.calificacion}"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 🔥 BOTÓN AGENDAR
                Button(
                    onClick = onAgendarClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Agendar cita")
                }
            }
        }
    }
}