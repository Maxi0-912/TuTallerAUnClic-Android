package com.manuel.tutalleraunclic.ui.screens.citas

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import java.util.TimeZone

private val GradientCita = listOf(Color(0xFF4F8EF7), Color(0xFF7C5CBF))

private val HorasDisponibles = listOf(
    "08:00", "09:00", "10:00", "11:00", "12:00",
    "13:00", "14:00", "15:00", "16:00", "17:00", "18:00"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CrearCitaScreen(
    establecimientoId: Int,
    servicioId: Int,
    onSuccess: () -> Unit,
    onBack: () -> Unit = {},
    viewModel: CitaViewModel = hiltViewModel()
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.setIds(establecimientoId, servicioId)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is UiEvent.ShowError   -> snackbarHostState.showSnackbar(event.message)
                is UiEvent.ShowMessage -> { snackbarHostState.showSnackbar(event.message); onSuccess() }
            }
        }
    }

    // ── Date Picker ──────────────────────────────────────────────────────────
    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showDatePicker = false
                    datePickerState.selectedDateMillis?.let { millis ->
                        val utcCalendar = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                            timeInMillis = millis
                        }
                        val fecha = "%04d-%02d-%02d".format(
                            utcCalendar.get(Calendar.YEAR),
                            utcCalendar.get(Calendar.MONTH) + 1,
                            utcCalendar.get(Calendar.DAY_OF_MONTH)
                        )
                        viewModel.seleccionarFecha(fecha)
                    }
                }) { Text("Aceptar") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    // ── Scaffold ─────────────────────────────────────────────────────────────
    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {

            // ── Gradient header ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.linearGradient(GradientCita))
                    .padding(horizontal = 20.dp, vertical = 28.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver",
                            tint = Color.White
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Image(
                        painter = painterResource(id = R.drawable.logo_solo),
                        contentDescription = "Logo",
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Agendar Cita",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = "Programa tu servicio",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {

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
                            text = "Tu vehículo",
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
                            label = { Text("¿Qué necesita tu vehículo? (opcional)") },
                            leadingIcon = { Icon(Icons.Default.Build, null) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            maxLines = 3
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // ── Card: Fecha y hora ───────────────────────────────────────
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
                            text = "Fecha y hora",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                        )

                        // Fecha selector
                        OutlinedCard(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarToday,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = viewModel.fecha.ifEmpty { "Seleccionar fecha" },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = if (viewModel.fecha.isEmpty())
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                    else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        // Hora — chips de horas disponibles
                        Text(
                            text = "Hora",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            HorasDisponibles.forEach { hora ->
                                val horaValue = "$hora:00"
                                val selected = viewModel.hora == horaValue
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.onHoraChange(horaValue) },
                                    label = { Text(hora, fontSize = 13.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                                        selectedLabelColor = Color.White
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = selected,
                                        borderColor = MaterialTheme.colorScheme.primary,
                                        selectedBorderColor = MaterialTheme.colorScheme.primary
                                    )
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(28.dp))

                // ── Botón confirmar ──────────────────────────────────────────
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            if (!viewModel.isSaving)
                                Brush.linearGradient(GradientCita)
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
                            onClick = { viewModel.crearCita() },
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = "Confirmar Cita",
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
