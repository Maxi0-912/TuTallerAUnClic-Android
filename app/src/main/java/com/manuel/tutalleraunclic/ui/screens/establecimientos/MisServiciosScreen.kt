package com.manuel.tutalleraunclic.ui.screens.establecimientos

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.manuel.tutalleraunclic.R
import com.manuel.tutalleraunclic.data.model.response.ServicioEmpresaResponse
import com.manuel.tutalleraunclic.ui.components.AppAlertDialog
import com.manuel.tutalleraunclic.viewmodel.MisServiciosViewModel
import com.manuel.tutalleraunclic.viewmodel.UiEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisServiciosScreen(
    onBack: () -> Unit,
    onCrear: () -> Unit,
    onEditar: (Int) -> Unit,
    viewModel: MisServiciosViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var servicioAEliminar by remember { mutableStateOf<Int?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) { viewModel.cargar() }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.cargar()
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

    val filtradas by remember(state.servicios, state.filtroEstablecimientoId, state.busqueda) {
        derivedStateOf {
            state.servicios.filter { s ->
                val pasaEstab = state.filtroEstablecimientoId == null || s.establecimiento_id == state.filtroEstablecimientoId
                val pasaBusqueda = state.busqueda.isBlank() || s.nombre.contains(state.busqueda, ignoreCase = true)
                pasaEstab && pasaBusqueda
            }
        }
    }

    val agrupados by remember(filtradas) {
        derivedStateOf { filtradas.groupBy { it.establecimiento_nombre ?: "Sin establecimiento" } }
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
                        Text("Servicios", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onCrear, icon = {
                Icon(Icons.Default.Add, contentDescription = null)
            }, text = { Text("Nuevo servicio") })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            servicioAEliminar?.let { id ->
                AppAlertDialog(
                    onDismissRequest = { servicioAEliminar = null },
                    confirmButton = {
                        Button(onClick = {
                            viewModel.eliminarServicio(id)
                            servicioAEliminar = null
                        }) { Text("Sí, eliminar") }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { servicioAEliminar = null }) { Text("Cancelar") }
                    },
                    title = { Text("Eliminar servicio") },
                    text = { Text("Esta acción no se puede deshacer.") }
                )
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        "${state.servicios.size} servicio${if (state.servicios.size != 1) "s" else ""} registrados",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                item {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = state.busqueda,
                            onValueChange = viewModel::setBusqueda,
                            placeholder = { Text("Buscar servicio...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (state.establecimientos.size > 1) {
                            var expanded by remember { mutableStateOf(false) }
                            val seleccionado = state.establecimientos.firstOrNull { it.id == state.filtroEstablecimientoId }
                            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
                                OutlinedTextField(
                                    value = seleccionado?.nombre ?: "Todos los establecimientos",
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
                                    state.establecimientos.forEach { est ->
                                        DropdownMenuItem(text = { Text(est.nombre) }, onClick = {
                                            viewModel.setFiltroEstablecimiento(est.id)
                                            expanded = false
                                        })
                                    }
                                }
                            }
                        }
                    }
                }

                when {
                    state.isLoading && state.servicios.isEmpty() -> {
                        item {
                            Box(Modifier.fillMaxWidth().padding(vertical = 64.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator()
                            }
                        }
                    }
                    filtradas.isEmpty() -> {
                        item {
                            Box(Modifier.fillMaxWidth().padding(vertical = 64.dp), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🔧", style = MaterialTheme.typography.displaySmall)
                                    Spacer(Modifier.height(8.dp))
                                    Text(
                                        "No hay servicios registrados",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                    Spacer(Modifier.height(8.dp))
                                    TextButton(onClick = onCrear) { Text("Agregar el primero") }
                                }
                            }
                        }
                    }
                    else -> {
                        agrupados.forEach { (nombreEstab, items) ->
                            item(key = "header_$nombreEstab") {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(nombreEstab, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            "${items.size} servicio${if (items.size != 1) "s" else ""}",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                            items(items, key = { it.id }) { servicio ->
                                ServicioEmpresaCard(
                                    servicio = servicio,
                                    onEditar = { onEditar(servicio.id) },
                                    onEliminar = { servicioAEliminar = servicio.id }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ServicioEmpresaCard(
    servicio: ServicioEmpresaResponse,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(shape = RoundedCornerShape(14.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(servicio.nombre, fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)
                if (!servicio.tipo_servicio_nombre.isNullOrBlank()) {
                    Text(
                        servicio.tipo_servicio_nombre,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            Row {
                IconButton(onClick = onEditar) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar", tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
                IconButton(onClick = onEliminar) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}
