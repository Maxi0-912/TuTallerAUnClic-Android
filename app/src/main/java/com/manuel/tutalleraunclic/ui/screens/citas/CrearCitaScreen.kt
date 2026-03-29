package com.manuel.tutalleraunclic.ui.screens.citas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.manuel.tutalleraunclic.core.navigation.Routes
import com.manuel.tutalleraunclic.viewmodel.CitaViewModel
import com.manuel.tutalleraunclic.viewmodel.CitaState
import com.manuel.tutalleraunclic.ui.components.SelectorField
import com.manuel.tutalleraunclic.data.model.entity.Agenda
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearCitaScreen(
    navController: NavController,
    establecimientoId: Int,
    viewModel: CitaViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var fecha by remember { mutableStateOf("") }
    var comentario by remember { mutableStateOf("") }

    var showDatePicker by remember { mutableStateOf(false) }

    // 🔥 NUEVO: agenda seleccionada
    var agendaSeleccionada by remember { mutableStateOf<Agenda?>(null) }

    // 🔥 FEEDBACK
    LaunchedEffect(state) {
        when (state) {

            is CitaState.Success -> {
                snackbarHostState.showSnackbar("✅ Cita agendada")
                viewModel.resetState()

                navController.navigate(Routes.MIS_CITAS) {
                    popUpTo(Routes.CITA) { inclusive = true }
                }
            }

            is CitaState.Error -> {
                snackbarHostState.showSnackbar(
                    (state as CitaState.Error).message
                )
                viewModel.resetState()
            }

            else -> {}
        }
    }

    // 📅 DATE PICKER
    if (showDatePicker) {
        val dateState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateState.selectedDateMillis?.let {
                        val cal = Calendar.getInstance().apply { timeInMillis = it }
                        fecha = "%04d-%02d-%02d".format(
                            cal.get(Calendar.YEAR),
                            cal.get(Calendar.MONTH) + 1,
                            cal.get(Calendar.DAY_OF_MONTH)
                        )

                        // 🔥 PASO 6: cargar agendas automáticamente
                        viewModel.cargarAgendas(establecimientoId, fecha)

                        agendaSeleccionada = null
                    }
                    showDatePicker = false
                }) { Text("Aceptar") }
            }
        ) {
            DatePicker(state = dateState)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Agendar cita") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // 🔥 CARD
            Card(
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    Text("Selecciona fecha y hora")

                    // 📅 FECHA
                    SelectorField(
                        label = "Fecha",
                        value = fecha,
                        icon = Icons.Default.CalendarToday,
                        onClick = { showDatePicker = true }
                    )

                    // 🔥 PASO 5: HORARIOS DINÁMICOS
                    Text("Horarios disponibles")

                    when {
                        viewModel.isLoadingAgendas -> {
                            CircularProgressIndicator()
                        }

                        viewModel.agendas.isEmpty() -> {
                            Text("No hay horarios disponibles 😔")
                        }

                        else -> {
                            FlowRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                viewModel.agendas.forEach { agenda ->

                                    val selected = agendaSeleccionada?.id == agenda.id

                                    FilterChip(
                                        selected = selected,
                                        onClick = { agendaSeleccionada = agenda },
                                        label = { Text(agenda.hora) },
                                        leadingIcon = {
                                            if (selected) {
                                                Icon(Icons.Default.Check, null)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 💬 COMENTARIO
                    OutlinedTextField(
                        value = comentario,
                        onValueChange = { comentario = it },
                        label = { Text("Comentario") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 🔥 PASO 7: BOTÓN INTELIGENTE
            Button(
                onClick = {

                    if (fecha.isBlank()) {
                        viewModel.setError("Selecciona fecha")
                        return@Button
                    }

                    if (agendaSeleccionada == null) {
                        viewModel.setError("Selecciona un horario")
                        return@Button
                    }

                    viewModel.crearCita(
                        establecimientoId,
                        agendaSeleccionada!!.id,
                        fecha,
                        comentario
                    )
                },
                enabled = agendaSeleccionada != null && state !is CitaState.Loading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {

                if (state is CitaState.Loading) {
                    CircularProgressIndicator(strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Check, null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Confirmar cita")
                }
            }
        }
    }
}