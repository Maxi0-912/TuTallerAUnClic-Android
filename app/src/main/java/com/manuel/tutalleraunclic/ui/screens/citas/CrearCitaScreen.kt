package com.manuel.tutalleraunclic.ui.screens.citas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.manuel.tutalleraunclic.viewmodel.CitaViewModel
import com.manuel.tutalleraunclic.data.model.entity.Agenda
import androidx.compose.foundation.layout.ExperimentalLayoutApi

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CrearCitaScreen(
    viewModel: CitaViewModel,
    establecimientoId: Int,
    servicioId: Int
) {

    val state by viewModel.state.collectAsState()
    var comentario by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {

        // 🔥 HEADER
        Text(
            text = "Agendar cita",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // =========================
        // 📅 FECHAS
        // =========================
        Text(
            text = "Selecciona una fecha",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            listOf("2026-04-01", "2026-04-02", "2026-04-03").forEach { fecha ->

                val isSelected = fecha == state.fechaSeleccionada

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        viewModel.seleccionarFecha(fecha, establecimientoId)
                    },
                    label = { Text(fecha) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // =========================
        // ⏳ LOADING
        // =========================
        if (state.isLoading) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(16.dp))
        }

        // =========================
        // ⏰ HORAS
        // =========================
        Text(
            text = "Horas disponibles",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (state.horarios.isEmpty() && state.fechaSeleccionada.isNotBlank()) {
            Text(
                text = "No hay horarios disponibles",
                color = Color.Gray
            )
        }

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            state.horarios.forEach { hora ->

                val isSelected = hora == state.horaSeleccionada

                FilterChip(
                    selected = isSelected,
                    onClick = {
                        viewModel.seleccionarHora(hora)
                    },
                    label = { Text(hora) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // =========================
        // 📝 COMENTARIO
        // =========================
        Text(
            text = "Describe lo que necesitas",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = comentario,
            onValueChange = { comentario = it },
            placeholder = { Text("Ej: lavado completo + aspirado interno") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(24.dp))

        // =========================
        // 🚀 BOTÓN
        // =========================
        Button(
            onClick = {
                viewModel.crearCita(
                    establecimientoId = establecimientoId,
                    servicioId = servicioId,
                    descripcion = comentario.ifBlank { "Sin comentario" }
                )
            },
            enabled = state.fechaSeleccionada.isNotBlank() && state.horaSeleccionada != null,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Agendar cita")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // =========================
        // ❌ ERROR
        // =========================
        state.error?.let {
            Text(
                text = it,
                color = Color.Red
            )
        }

        // =========================
        // ✅ SUCCESS
        // =========================
        if (state.success) {
            Text(
                text = "Cita creada correctamente ✅",
                color = Color(0xFF2E7D32)
            )
        }
    }
}