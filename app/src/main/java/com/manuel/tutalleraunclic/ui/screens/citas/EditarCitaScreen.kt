package com.manuel.tutalleraunclic.ui.screens.citas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.manuel.tutalleraunclic.viewmodel.CitaViewModel
import com.manuel.tutalleraunclic.viewmodel.UiEvent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditarCitaScreen(
    citaId: Int,
    onSuccess: () -> Unit,
    onBack: () -> Unit = {},
    viewModel: CitaViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(citaId) {
        viewModel.cargarCita(citaId)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowError   -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.ShowMessage -> {
                    snackbarHostState.showSnackbar(event.message)
                    onSuccess()
                }
            }
        }
    }

    // DATE PICKER
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    datePickerState.selectedDateMillis?.let { millis ->
                        val cal = java.util.Calendar.getInstance().apply { timeInMillis = millis }
                        val fecha = "%04d-%02d-%02d".format(
                            cal.get(java.util.Calendar.YEAR),
                            cal.get(java.util.Calendar.MONTH) + 1,
                            cal.get(java.util.Calendar.DAY_OF_MONTH)
                        )
                        viewModel.seleccionarFecha(fecha)
                    }
                }) { Text("Aceptar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    // TIME PICKER
    val timePickerState = rememberTimePickerState()
    var showTimePicker by remember { mutableStateOf(false) }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showTimePicker = false
                    val hora = "%02d:%02d:00".format(timePickerState.hour, timePickerState.minute)
                    viewModel.onHoraChange(hora)
                }) { Text("Aceptar") }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Editar cita") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { padding ->

        if (viewModel.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) { CircularProgressIndicator() }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text("Editar Cita", style = MaterialTheme.typography.titleLarge)

            // PLACA
            OutlinedTextField(
                value         = viewModel.placa,
                onValueChange = viewModel::onPlacaChange,
                label         = { Text("Placa del vehículo") },
                leadingIcon   = { Icon(Icons.Default.DirectionsCar, null) },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                singleLine    = true
            )

            // SERVICIO DROPDOWN
            if (viewModel.servicios.isNotEmpty()) {
                var expandedServicio by remember { mutableStateOf(false) }
                val servicioNombre = viewModel.servicios
                    .find { it.id == viewModel.servicioSeleccionadoId }?.nombre
                    ?: "Seleccionar servicio"

                ExposedDropdownMenuBox(
                    expanded = expandedServicio,
                    onExpandedChange = { expandedServicio = !expandedServicio }
                ) {
                    OutlinedTextField(
                        value         = servicioNombre,
                        onValueChange = {},
                        readOnly      = true,
                        label         = { Text("Servicio") },
                        leadingIcon   = { Icon(Icons.Default.Build, null) },
                        trailingIcon  = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedServicio) },
                        modifier      = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        shape         = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expandedServicio,
                        onDismissRequest = { expandedServicio = false }
                    ) {
                        viewModel.servicios.forEach { servicio ->
                            DropdownMenuItem(
                                text    = { Text(servicio.nombre) },
                                onClick = {
                                    viewModel.onServicioChange(servicio.id)
                                    expandedServicio = false
                                }
                            )
                        }
                    }
                }
            }

            // FECHA
            ElevatedCard(
                onClick = { showDatePicker = true },
                shape   = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.CalendarToday, null, tint = MaterialTheme.colorScheme.primary)
                    Text(viewModel.fecha.ifEmpty { "Seleccionar fecha" })
                }
            }

            // HORA
            ElevatedCard(
                onClick = { showTimePicker = true },
                shape   = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.AccessTime, null, tint = MaterialTheme.colorScheme.primary)
                    Text(viewModel.hora.ifEmpty { "Seleccionar hora" })
                }
            }

            // NOTAS / OBSERVACIONES
            OutlinedTextField(
                value         = viewModel.descripcion,
                onValueChange = viewModel::onDescripcionChange,
                label         = { Text("Notas u observaciones (opcional)") },
                modifier      = Modifier.fillMaxWidth(),
                shape         = RoundedCornerShape(12.dp),
                maxLines      = 3
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick  = { viewModel.actualizarCita(citaId) },
                enabled  = !viewModel.isSaving,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (viewModel.isSaving) {
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp, modifier = Modifier.size(22.dp))
                } else {
                    Text("Actualizar Cita")
                }
            }
        }
    }
}
