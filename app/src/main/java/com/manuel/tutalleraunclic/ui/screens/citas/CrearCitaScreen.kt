package com.manuel.tutalleraunclic.ui.screens.citas

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.manuel.tutalleraunclic.viewmodel.CitaViewModel
import com.manuel.tutalleraunclic.viewmodel.UiEvent
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearCitaScreen(
    establecimientoId: Int,
    servicioId: Int,
    onSuccess: () -> Unit,
    onBack: () -> Unit = {},
    viewModel: CitaViewModel = hiltViewModel()
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val interactionSource = remember { MutableInteractionSource() }

    // INIT
    LaunchedEffect(Unit) {
        viewModel.setIds(establecimientoId, servicioId)
    }

    // EVENTOS
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowError -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                    onSuccess()
                }
            }
        }
    }

    // ---------------- DATE PICKER ----------------
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false

                    datePickerState.selectedDateMillis?.let { millis ->

                        val calendar = Calendar.getInstance().apply {
                            timeInMillis = millis
                        }

                        val year = calendar.get(Calendar.YEAR)
                        val month = calendar.get(Calendar.MONTH) + 1
                        val day = calendar.get(Calendar.DAY_OF_MONTH)

                        val fecha = "%04d-%02d-%02d".format(year, month, day)

                        viewModel.seleccionarFecha(fecha)
                    }
                }) {
                    Text("Aceptar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // ---------------- TIME PICKER ----------------
    val timePickerState = rememberTimePickerState()
    var showTimePicker by remember { mutableStateOf(false) }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showTimePicker = false

                    val hora = "%02d:%02d:00".format(
                        timePickerState.hour,
                        timePickerState.minute
                    )

                    viewModel.onHoraChange(hora)
                }) {
                    Text("Aceptar")
                }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    // ---------------- UI ----------------
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Agendar Cita") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp)
        ) {

            Text(
                text = "Programa tu servicio",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CARD PRINCIPAL
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(modifier = Modifier.padding(20.dp)) {

                    Text(
                        "Detalles del servicio",
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // PLACA
                    OutlinedTextField(
                        value = viewModel.placa,
                        onValueChange = viewModel::onPlacaChange,
                        label = { Text("Placa del vehículo") },
                        leadingIcon = { Icon(Icons.Default.DirectionsCar, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // DESCRIPCIÓN
                    OutlinedTextField(
                        value = viewModel.descripcion,
                        onValueChange = viewModel::onDescripcionChange,
                        label = { Text("¿Qué necesita tu vehículo? (opcional)") },
                        leadingIcon = { Icon(Icons.Default.Build, null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // FECHA (tipo botón moderno)
                    ElevatedCard(
                        onClick = { showDatePicker = true },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                viewModel.fecha.ifEmpty { "Seleccionar fecha" }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // HORA (tipo botón moderno)
                    ElevatedCard(
                        onClick = { showTimePicker = true },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row {
                                Icon(Icons.Default.AccessTime, null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    viewModel.hora.ifEmpty { "Seleccionar hora" }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // ANIMACIÓN
                    AnimatedVisibility(visible = viewModel.fecha.isNotEmpty()) {
                        Text(
                            "Fecha seleccionada ✔",
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // BOTÓN
            Button(
                onClick = { viewModel.crearCita() },
                enabled = !viewModel.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(18.dp)
            ) {

                if (viewModel.isSaving) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    Text("Confirmar Cita")
                }
            }
        }
    }
}