package com.manuel.tutalleraunclic.ui.screens.citas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.manuel.tutalleraunclic.R
import com.manuel.tutalleraunclic.viewmodel.CitaViewModel
import com.manuel.tutalleraunclic.viewmodel.UiEvent
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar

private val GradientEditar = listOf(Color(0xFF4F8EF7), Color(0xFF7C5CBF))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarCitaScreen(
    citaId: Int,
    onSuccess: () -> Unit,
    onBack: () -> Unit = {},
    viewModel: CitaViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    // Próximos 7 días: label "Lun 19" → fecha "YYYY-MM-DD"
    val diasChips = remember {
        val result = mutableListOf<Pair<String, String>>()
        val cal = Calendar.getInstance()
        val nombres = arrayOf("Dom", "Lun", "Mar", "Mié", "Jue", "Vie", "Sáb")
        repeat(7) {
            val label = "${nombres[cal.get(Calendar.DAY_OF_WEEK) - 1]} ${cal.get(Calendar.DAY_OF_MONTH)}"
            val fecha = "%04d-%02d-%02d".format(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH) + 1,
                cal.get(Calendar.DAY_OF_MONTH)
            )
            result += label to fecha
            cal.add(Calendar.DAY_OF_MONTH, 1)
        }
        result
    }

    // Horas 08:00–18:00: label "08:00" → valor "08:00:00"
    val horasChips = remember {
        (8..18).map { h -> "%02d:00".format(h) to "%02d:00:00".format(h) }
    }

    LaunchedEffect(citaId) { viewModel.cargarCita(citaId) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowError   -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.ShowMessage -> { snackbarHostState.showSnackbar(event.message); onSuccess() }
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->

        if (viewModel.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Botón atrás ──────────────────────────────────────────────────
            Row(modifier = Modifier.fillMaxWidth().padding(start = 4.dp, top = 8.dp)) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
            }

            // ── Logo + título ────────────────────────────────────────────────
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_solo),
                    contentDescription = "Tu Taller a un Clic",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(14.dp))
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Editar Cita",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ── Card: Tu vehículo ────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Tu vehículo",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                        OutlinedTextField(
                            value = viewModel.placa,
                            onValueChange = viewModel::onPlacaChange,
                            label = { Text("Placa del vehículo") },
                            leadingIcon = { Icon(Icons.Default.DirectionsCar, null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = viewModel.descripcion,
                            onValueChange = viewModel::onDescripcionChange,
                            label = { Text("Notas u observaciones (opcional)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            maxLines = 3
                        )
                    }
                }

                // ── Card: Servicio ───────────────────────────────────────────
                if (viewModel.servicios.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.large,
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                "Servicio",
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                            )
                            var expandedServicio by remember { mutableStateOf(false) }
                            val servicioNombre = viewModel.servicios
                                .find { it.id == viewModel.servicioSeleccionadoId }?.nombre
                                ?: "Seleccionar servicio"

                            ExposedDropdownMenuBox(
                                expanded = expandedServicio,
                                onExpandedChange = { expandedServicio = !expandedServicio }
                            ) {
                                OutlinedTextField(
                                    value = servicioNombre,
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Servicio") },
                                    leadingIcon = { Icon(Icons.Default.Build, null) },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedServicio) },
                                    modifier = Modifier
                                        .menuAnchor()
                                        .fillMaxWidth(),
                                    shape = RoundedCornerShape(14.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = expandedServicio,
                                    onDismissRequest = { expandedServicio = false }
                                ) {
                                    viewModel.servicios.forEach { servicio ->
                                        DropdownMenuItem(
                                            text = { Text(servicio.nombre) },
                                            onClick = {
                                                viewModel.onServicioChange(servicio.id)
                                                expandedServicio = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Card: Fecha ──────────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Fecha",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            diasChips.forEach { (label, fecha) ->
                                FilterChip(
                                    selected = viewModel.fecha == fecha,
                                    onClick  = { viewModel.seleccionarFecha(fecha) },
                                    label    = { Text(label, fontSize = 13.sp) }
                                )
                            }
                        }
                    }
                }

                // ── Card: Hora ───────────────────────────────────────────────
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.large,
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Hora",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Spacer(Modifier.height(12.dp))
                        horasChips.chunked(3).forEach { fila ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                fila.forEach { (label, hora) ->
                                    FilterChip(
                                        selected = viewModel.hora == hora,
                                        onClick  = { viewModel.onHoraChange(hora) },
                                        label    = { Text(label, fontSize = 13.sp) },
                                        modifier = Modifier.weight(1f)
                                    )
                                }
                                repeat(3 - fila.size) { Spacer(Modifier.weight(1f)) }
                            }
                            Spacer(Modifier.height(4.dp))
                        }
                    }
                }

                Spacer(Modifier.height(4.dp))

                // ── Botón gradiente ──────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (!viewModel.isSaving)
                                Brush.linearGradient(GradientEditar)
                            else Brush.linearGradient(
                                listOf(
                                    Color(0xFF4F8EF7).copy(alpha = 0.55f),
                                    Color(0xFF7C5CBF).copy(alpha = 0.55f)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (viewModel.isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.5.dp,
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        TextButton(
                            onClick = { viewModel.actualizarCita(citaId) },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "Actualizar Cita",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(Modifier.height(24.dp))
            }
        }
    }
}
