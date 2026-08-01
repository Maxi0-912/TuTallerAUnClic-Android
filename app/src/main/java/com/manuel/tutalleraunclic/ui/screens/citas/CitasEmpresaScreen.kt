package com.manuel.tutalleraunclic.ui.screens.citas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.manuel.tutalleraunclic.R
import com.manuel.tutalleraunclic.data.model.response.CitaEmpresaResponse
import com.manuel.tutalleraunclic.ui.components.AppAlertDialog
import com.manuel.tutalleraunclic.viewmodel.CitasEmpresaViewModel
import com.manuel.tutalleraunclic.viewmodel.UiEvent
import kotlinx.coroutines.launch

private data class EstadoConfig(val value: String, val label: String, val color: Color)

private val ESTADOS = listOf(
    EstadoConfig("pendiente", "Pendiente", Color(0xFFCA8A04)),
    EstadoConfig("confirmada", "Confirmada", Color(0xFF2563EB)),
    EstadoConfig("finalizada", "Finalizada", Color(0xFF16A34A)),
    EstadoConfig("cancelada", "Cancelada", Color(0xFFDC2626)),
)

private fun estadoConfig(estado: String): EstadoConfig =
    ESTADOS.firstOrNull { it.value == estado.lowercase() } ?: ESTADOS[0]

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitasEmpresaScreen(
    viewModel: CitasEmpresaViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    var citaParaEstado by remember { mutableStateOf<CitaEmpresaResponse?>(null) }
    var citaParaComentario by remember { mutableStateOf<CitaEmpresaResponse?>(null) }

    LaunchedEffect(Unit) { viewModel.cargarCitas() }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.cargarCitas()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            val mensaje = when (event) {
                is UiEvent.ShowMessage -> event.message
                is UiEvent.ShowError -> event.message
            }
            scope.launch { snackbarHostState.showSnackbar(mensaje) }
        }
    }

    // Cierra los diálogos automáticamente cuando la acción terminó con éxito
    LaunchedEffect(state.accionEnProgreso, state.isLoading) {
        if (!state.accionEnProgreso && !state.isLoading) {
            citaParaEstado = null
            citaParaComentario = null
        }
    }

    val establecimientos by remember(state.citas) {
        derivedStateOf {
            state.citas
                .map { it.establecimiento_id to (it.establecimiento_nombre ?: "Establecimiento") }
                .distinctBy { it.first }
        }
    }

    val contadores by remember(state.citas) {
        derivedStateOf {
            ESTADOS.associate { cfg -> cfg.value to state.citas.count { it.estado.lowercase() == cfg.value } }
        }
    }

    val filtradas by remember(state.citas, state.filtroEstado, state.filtroEstablecimientoId, state.busqueda) {
        derivedStateOf {
            state.citas.filter { cita ->
                val pasaEstado = state.filtroEstado == null || cita.estado.lowercase() == state.filtroEstado
                val pasaEstab = state.filtroEstablecimientoId == null || cita.establecimiento_id == state.filtroEstablecimientoId
                val pasaBusqueda = state.busqueda.isBlank() ||
                    cita.usuario_nombre.contains(state.busqueda, ignoreCase = true)
                pasaEstado && pasaEstab && pasaBusqueda
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
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
                        Text("Citas", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    "${state.citas.size} cita${if (state.citas.size != 1) "s" else ""} en total",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // ── Contadores 2x2 ──────────────────────────────────────────────
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ESTADOS.chunked(2).forEach { fila ->
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            fila.forEach { cfg ->
                                ContadorEstado(
                                    cfg = cfg,
                                    cantidad = contadores[cfg.value] ?: 0,
                                    seleccionado = state.filtroEstado == cfg.value,
                                    onClick = { viewModel.setFiltroEstado(cfg.value) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            // ── Buscador y filtros ───────────────────────────────────────────
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = state.busqueda,
                        onValueChange = viewModel::setBusqueda,
                        placeholder = { Text("Buscar por cliente...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (establecimientos.size > 1) {
                        var expanded by remember { mutableStateOf(false) }
                        val seleccionado = establecimientos.firstOrNull { it.first == state.filtroEstablecimientoId }
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                            OutlinedTextField(
                                value = seleccionado?.second ?: "Todos los establecimientos",
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Establecimiento") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.fillMaxWidth().menuAnchor()
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                DropdownMenuItem(text = { Text("Todos los establecimientos") }, onClick = {
                                    viewModel.setFiltroEstablecimiento(null)
                                    expanded = false
                                })
                                establecimientos.forEach { (id, nombre) ->
                                    DropdownMenuItem(text = { Text(nombre) }, onClick = {
                                        viewModel.setFiltroEstablecimiento(id)
                                        expanded = false
                                    })
                                }
                            }
                        }
                    }

                    var estadoExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(expanded = estadoExpanded, onExpandedChange = { estadoExpanded = it }) {
                        OutlinedTextField(
                            value = ESTADOS.firstOrNull { it.value == state.filtroEstado }?.label ?: "Todos los estados",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Estado") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = estadoExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = estadoExpanded, onDismissRequest = { estadoExpanded = false }) {
                            DropdownMenuItem(text = { Text("Todos los estados") }, onClick = {
                                viewModel.setFiltroEstado(null)
                                estadoExpanded = false
                            })
                            ESTADOS.forEach { cfg ->
                                DropdownMenuItem(text = { Text(cfg.label) }, onClick = {
                                    if (state.filtroEstado != cfg.value) viewModel.setFiltroEstado(cfg.value)
                                    estadoExpanded = false
                                })
                            }
                        }
                    }
                }
            }

            // ── Lista ─────────────────────────────────────────────────────────
            when {
                state.isLoading && state.citas.isEmpty() -> {
                    item {
                        Box(Modifier.fillMaxWidth().padding(vertical = 64.dp), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    }
                }
                filtradas.isEmpty() -> {
                    item {
                        Box(Modifier.fillMaxWidth().padding(vertical = 64.dp), contentAlignment = Alignment.Center) {
                            Text(
                                "No hay citas con estos filtros",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
                else -> {
                    items(filtradas, key = { it.id }) { cita ->
                        TarjetaCitaEmpresa(
                            cita = cita,
                            onCambiarEstado = { citaParaEstado = cita },
                            onAgregarComentario = { citaParaComentario = cita }
                        )
                    }
                }
            }
        }
    }

    citaParaEstado?.let { cita ->
        DialogCambiarEstado(
            cita = cita,
            saving = state.accionEnProgreso,
            onDismiss = { citaParaEstado = null },
            onConfirmar = { nuevoEstado -> viewModel.cambiarEstado(cita.id, nuevoEstado) }
        )
    }

    citaParaComentario?.let { cita ->
        DialogComentario(
            cita = cita,
            saving = state.accionEnProgreso,
            onDismiss = { citaParaComentario = null },
            onConfirmar = { comentario -> viewModel.agregarComentario(cita.id, comentario) }
        )
    }
}

@Composable
private fun ContadorEstado(
    cfg: EstadoConfig,
    cantidad: Int,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(
            containerColor = if (seleccionado) cfg.color.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, if (seleccionado) cfg.color else MaterialTheme.colorScheme.outlineVariant)
    ) {
        Box(modifier = Modifier.clickable { onClick() }.padding(12.dp)) {
            Column {
                Text("$cantidad", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black)
                Text(
                    cfg.label + if (cantidad != 1) "s" else "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun BadgeEstado(estado: String) {
    val cfg = estadoConfig(estado)
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(cfg.color.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(cfg.label, color = cfg.color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun InfoBox(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        Text(value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun TarjetaCitaEmpresa(
    cita: CitaEmpresaResponse,
    onCambiarEstado: () -> Unit,
    onAgregarComentario: () -> Unit
) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text(cita.usuario_nombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    if (!cita.establecimiento_nombre.isNullOrBlank()) {
                        Text(
                            cita.establecimiento_nombre,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
                BadgeEstado(cita.estado)
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                InfoBox("Fecha", cita.fecha, modifier = Modifier.weight(1f))
                InfoBox("Hora", cita.hora.take(5), modifier = Modifier.weight(1f))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                InfoBox("Vehículo", cita.vehiculo_placa ?: "—", modifier = Modifier.weight(1f))
                InfoBox("Teléfono", cita.usuario_telefono ?: "—", modifier = Modifier.weight(1f))
            }

            val servicio = cita.servicio_nombre ?: cita.servicio_texto
            if (!servicio.isNullOrBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text("Servicio solicitado", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                    Text(servicio, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                }
            }

            if (!cita.descripcion.isNullOrBlank()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text(
                        "Observación del cliente",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(cita.descripcion, style = MaterialTheme.typography.bodyMedium)
                }
            }

            if (cita.estado.lowercase() == "finalizada") {
                if (!cita.comentario_empresa.isNullOrBlank()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF16A34A).copy(alpha = 0.1f))
                            .padding(horizontal = 10.dp, vertical = 8.dp)
                    ) {
                        Text("Comentario de la empresa", style = MaterialTheme.typography.labelSmall, color = Color(0xFF16A34A))
                        Text(cita.comentario_empresa, style = MaterialTheme.typography.bodyMedium)
                    }
                } else {
                    OutlinedButton(
                        onClick = onAgregarComentario,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF16A34A))
                    ) {
                        Text("+ Agregar comentario del servicio")
                    }
                }
            }

            if (cita.estado.lowercase() != "cancelada" && cita.estado.lowercase() != "finalizada") {
                OutlinedButton(
                    onClick = onCambiarEstado,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Cambiar estado")
                }
            }
        }
    }
}

@Composable
private fun DialogCambiarEstado(
    cita: CitaEmpresaResponse,
    saving: Boolean,
    onDismiss: () -> Unit,
    onConfirmar: (String) -> Unit
) {
    var estadoSeleccionado by remember(cita.id) { mutableStateOf(cita.estado.lowercase()) }
    val opciones = remember(cita.estado) {
        ESTADOS.filter { it.value != "pendiente" || cita.estado.lowercase() == "pendiente" }
    }

    AppAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Cambiar estado") },
        text = {
            Column {
                Text(
                    "${cita.usuario_nombre} · ${cita.establecimiento_nombre ?: ""} · ${cita.fecha}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(12.dp))
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    opciones.forEach { cfg ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { estadoSeleccionado = cfg.value }
                        ) {
                            RadioButton(
                                selected = estadoSeleccionado == cfg.value,
                                onClick = { estadoSeleccionado = cfg.value },
                                colors = RadioButtonDefaults.colors(selectedColor = cfg.color)
                            )
                            Text(cfg.label)
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirmar(estadoSeleccionado) }, enabled = !saving) {
                if (saving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                } else {
                    Text("Confirmar")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, enabled = !saving) { Text("Cancelar") }
        }
    )
}

@Composable
private fun DialogComentario(
    cita: CitaEmpresaResponse,
    saving: Boolean,
    onDismiss: () -> Unit,
    onConfirmar: (String) -> Unit
) {
    var comentario by remember(cita.id) { mutableStateOf(cita.comentario_empresa ?: "") }

    AppAlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Comentario del servicio") },
        text = {
            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Text(
                    "${cita.usuario_nombre} · ${cita.establecimiento_nombre ?: ""} · ${cita.fecha}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = comentario,
                    onValueChange = { comentario = it },
                    placeholder = { Text("Escribe las observaciones del servicio realizado...") },
                    minLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { if (comentario.isNotBlank()) onConfirmar(comentario.trim()) },
                enabled = !saving && comentario.isNotBlank()
            ) {
                if (saving) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.White)
                } else {
                    Text("Guardar comentario")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, enabled = !saving) { Text("Cancelar") }
        }
    )
}
