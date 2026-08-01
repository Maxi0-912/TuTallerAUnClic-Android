package com.manuel.tutalleraunclic.ui.screens.establecimientos

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.manuel.tutalleraunclic.R
import com.manuel.tutalleraunclic.viewmodel.ServicioFormViewModel
import com.manuel.tutalleraunclic.viewmodel.UiEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicioFormScreen(
    servicioId: Int?,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: ServicioFormViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.inicializar(servicioId) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            val mensaje = when (event) {
                is UiEvent.ShowMessage -> event.message
                is UiEvent.ShowError -> event.message
            }
            scope.launch { snackbarHostState.showSnackbar(mensaje) }
        }
    }

    LaunchedEffect(state.guardadoExitoso) {
        if (state.guardadoExitoso) onSuccess()
    }

    // Filtra los tipos de servicio según si el establecimiento elegido es taller o lavadero
    val tiposFiltrados by remember(state.establecimientoId, state.tiposServicio, state.establecimientos) {
        derivedStateOf {
            val nombreTipoEstab = state.establecimientos.firstOrNull { it.id == state.establecimientoId }
                ?.tipo_nombre?.lowercase() ?: ""
            when {
                state.establecimientoId == null -> state.tiposServicio
                nombreTipoEstab.contains("taller") -> state.tiposServicio.filter { !it.nombre.lowercase().contains("lavado") }
                nombreTipoEstab.contains("lavadero") -> state.tiposServicio.filter { it.nombre.lowercase().contains("lavado") }
                else -> state.tiposServicio
            }
        }
    }

    var tipoQuery by remember { mutableStateOf("") }
    var tipoMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(state.tipoServicioId, state.tiposServicio) {
        tipoQuery = state.tiposServicio.firstOrNull { it.id == state.tipoServicioId }?.nombre ?: tipoQuery
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_solo),
                            contentDescription = "Logo",
                            modifier = Modifier.size(32.dp).clip(CircleShape)
                        )
                        Text(if (state.esEdicion) "Editar servicio" else "Nuevo servicio", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        if (state.isLoading) {
            Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            OutlinedTextField(
                value = state.nombre,
                onValueChange = viewModel::setNombre,
                label = { Text("Nombre del servicio *") },
                placeholder = { Text("ej: Cambio de aceite") },
                modifier = Modifier.fillMaxWidth()
            )

            // ── Establecimiento ───────────────────────────────────────────────
            var estabExpanded by remember { mutableStateOf(false) }
            val estabSeleccionado = state.establecimientos.firstOrNull { it.id == state.establecimientoId }
            ExposedDropdownMenuBox(expanded = estabExpanded, onExpandedChange = { estabExpanded = it }) {
                OutlinedTextField(
                    value = estabSeleccionado?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Establecimiento *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = estabExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = estabExpanded, onDismissRequest = { estabExpanded = false }) {
                    if (state.establecimientos.isEmpty()) {
                        DropdownMenuItem(text = { Text("No tienes establecimientos") }, onClick = {})
                    }
                    state.establecimientos.forEach { est ->
                        DropdownMenuItem(
                            text = { Text(est.nombre) },
                            onClick = {
                                viewModel.seleccionarEstablecimiento(est.id)
                                tipoQuery = ""
                                estabExpanded = false
                            }
                        )
                    }
                }
            }

            // ── Tipo de servicio (buscar o crear) ───────────────────────────────
            Column {
                ExposedDropdownMenuBox(
                    expanded = tipoMenuExpanded && state.establecimientoId != null,
                    onExpandedChange = { if (state.establecimientoId != null) tipoMenuExpanded = it }
                ) {
                    OutlinedTextField(
                        value = tipoQuery,
                        onValueChange = {
                            tipoQuery = it
                            tipoMenuExpanded = true
                        },
                        enabled = state.establecimientoId != null,
                        label = { Text("Tipo de servicio *") },
                        placeholder = { Text("Buscar o crear tipo...") },
                        trailingIcon = {
                            if (state.creandoTipo) {
                                CircularProgressIndicator(modifier = Modifier.size(18.dp))
                            } else {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoMenuExpanded)
                            }
                        },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    val coincidencias = tiposFiltrados.filter { it.nombre.contains(tipoQuery, ignoreCase = true) }
                    val hayExacto = tiposFiltrados.any { it.nombre.equals(tipoQuery.trim(), ignoreCase = true) }

                    ExposedDropdownMenu(
                        expanded = tipoMenuExpanded && state.establecimientoId != null,
                        onDismissRequest = { tipoMenuExpanded = false }
                    ) {
                        coincidencias.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo.nombre) },
                                onClick = {
                                    viewModel.seleccionarTipoServicio(tipo.id)
                                    tipoQuery = tipo.nombre
                                    tipoMenuExpanded = false
                                }
                            )
                        }
                        if (tipoQuery.isNotBlank() && !hayExacto) {
                            DropdownMenuItem(
                                text = { Text("+ Crear \"${tipoQuery.trim()}\"", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) },
                                onClick = {
                                    viewModel.crearTipoNuevo(tipoQuery.trim())
                                    tipoMenuExpanded = false
                                }
                            )
                        }
                    }
                }
                if (state.establecimientoId == null) {
                    Text(
                        "Selecciona primero un establecimiento",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = viewModel::guardar,
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Text(if (state.esEdicion) "Guardar cambios" else "Crear servicio")
                }
            }
        }
    }
}
