package com.manuel.tutalleraunclic.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding

import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.manuel.tutalleraunclic.data.model.entity.Cita


@Composable
fun CitaCard(cita: Cita) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Text(
                text = "Servicio: ${cita.servicio}",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Fecha: ${cita.fecha}")
            Text(text = "Hora: ${cita.hora}")

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Estado: ${cita.estado}",
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}