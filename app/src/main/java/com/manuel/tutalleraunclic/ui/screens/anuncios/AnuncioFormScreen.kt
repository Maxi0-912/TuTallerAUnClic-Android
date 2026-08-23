package com.manuel.tutalleraunclic.ui.screens.anuncios

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.manuel.tutalleraunclic.R
import com.manuel.tutalleraunclic.data.model.entity.TipoServicio
import com.manuel.tutalleraunclic.utils.fixImageUrl
import com.manuel.tutalleraunclic.viewmodel.AnuncioFormViewModel
import com.manuel.tutalleraunclic.viewmodel.UiEvent
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

private val TIPOS = listOf("imagen" to "Solo imagen", "imagen_texto" to "Imagen con texto", "imagen_boton" to "Imagen con botón")
private val CATEGORIAS = listOf("banner" to "Banner (inicio)", "oferta" to "Oferta")
private val UBICACIONES = listOf("perfil" to "Perfil", "banner" to "Banner")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnuncioFormScreen(
    anuncioId: Int?,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: AnuncioFormViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) { viewModel.inicializar(anuncioId) }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            val mensaje = when (event) {
                is UiEvent.ShowMessage -> event.message
                is UiEvent.ShowError -> event.message
            }
            scope.launch { snackbarHostState.showSnackbar(mensaje) }
        }
    }

    var mostrarPagoNequi by remember { mutableStateOf(false) }

    LaunchedEffect(state.guardadoExitoso) {
        if (state.guardadoExitoso) {
            if (state.requierePago) mostrarPagoNequi = true else onSuccess()
        }
    }

    if (mostrarPagoNequi) {
        PantallaPagoNequi(
            anuncioId = state.anuncioId ?: 0,
            onCerrar = {
                mostrarPagoNequi = false
                onSuccess()
            }
        )
    }

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri -> viewModel.setImagenUri(uri) }

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
                        Text(if (state.esEdicion) "Editar anuncio" else "Nuevo anuncio", fontWeight = FontWeight.Bold)
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

            // ── Estado / motivo de rechazo (solo lectura) ────────────────────
            if (state.esEdicion && state.estado != null) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = if (state.estado?.lowercase() == "rechazado")
                            MaterialTheme.colorScheme.errorContainer
                        else MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text("Estado: ${state.estado}", fontWeight = FontWeight.Bold)
                        if (!state.motivoRechazo.isNullOrBlank()) {
                            Text("Motivo: ${state.motivoRechazo}")
                        }
                    }
                }
            }

            // ── Imagen ─────────────────────────────────────────────────────────
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    val modelo: Any? = state.imagenUri ?: fixImageUrl(state.imagenUrlActual)
                    if (modelo != null) {
                        AsyncImage(
                            model = modelo,
                            contentDescription = "Imagen del anuncio",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Image, contentDescription = null)
                            Spacer(Modifier.height(4.dp))
                            Text("Toca para elegir una imagen")
                        }
                    }
                }
                TextButton(onClick = { launcher.launch("image/*") }) {
                    Text(if (state.imagenUri != null || state.imagenUrlActual != null) "Cambiar imagen" else "Elegir imagen")
                }
            }

            // ── Cupo (solo al crear) ──────────────────────────────────────────
            if (!state.esEdicion) {
                state.cupo?.let { cupo ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (cupo.gratis_restantes > 0)
                                MaterialTheme.colorScheme.secondaryContainer
                            else
                                MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(Modifier.padding(12.dp)) {
                            Text(
                                if (cupo.gratis_restantes > 0)
                                    "Te quedan ${cupo.gratis_restantes} anuncios gratis para este establecimiento"
                                else
                                    "Ya usaste tus anuncios gratis para este establecimiento",
                                fontWeight = FontWeight.Bold
                            )
                            if (cupo.proximo_requiere_pago) {
                                Text("El siguiente anuncio que crees será de pago.")
                            }
                        }
                    }
                }
            }

            // ── Establecimiento ───────────────────────────────────────────────
            var establecimientoExpanded by remember { mutableStateOf(false) }
            val establecimientoSeleccionado = state.misEstablecimientos.firstOrNull { it.id == state.establecimientoId }
            ExposedDropdownMenuBox(
                expanded = establecimientoExpanded,
                onExpandedChange = { establecimientoExpanded = it }
            ) {
                OutlinedTextField(
                    value = establecimientoSeleccionado?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Establecimiento") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = establecimientoExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = establecimientoExpanded,
                    onDismissRequest = { establecimientoExpanded = false }
                ) {
                    if (state.misEstablecimientos.isEmpty()) {
                        DropdownMenuItem(text = { Text("No tienes establecimientos") }, onClick = {})
                    }
                    state.misEstablecimientos.forEach { est ->
                        DropdownMenuItem(
                            text = { Text(est.nombre) },
                            onClick = {
                                viewModel.seleccionarEstablecimiento(est.id)
                                establecimientoExpanded = false
                            }
                        )
                    }
                }
            }

            // ── Título / descripción ────────────────────────────────────────
            OutlinedTextField(
                value = state.titulo,
                onValueChange = viewModel::setTitulo,
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.descripcion,
                onValueChange = viewModel::setDescripcion,
                label = { Text("Descripción") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            // ── Tipo ─────────────────────────────────────────────────────────
            var tipoExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = tipoExpanded, onExpandedChange = { tipoExpanded = it }) {
                OutlinedTextField(
                    value = TIPOS.firstOrNull { it.first == state.tipo }?.second ?: state.tipo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = tipoExpanded, onDismissRequest = { tipoExpanded = false }) {
                    TIPOS.forEach { (valor, etiqueta) ->
                        DropdownMenuItem(text = { Text(etiqueta) }, onClick = {
                            viewModel.setTipo(valor)
                            tipoExpanded = false
                        })
                    }
                }
            }

            // ── Categoría ────────────────────────────────────────────────────
            var categoriaExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(expanded = categoriaExpanded, onExpandedChange = { categoriaExpanded = it }) {
                OutlinedTextField(
                    value = CATEGORIAS.firstOrNull { it.first == state.categoria }?.second ?: state.categoria,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoriaExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = categoriaExpanded, onDismissRequest = { categoriaExpanded = false }) {
                    CATEGORIAS.forEach { (valor, etiqueta) ->
                        DropdownMenuItem(text = { Text(etiqueta) }, onClick = {
                            viewModel.setCategoria(valor)
                            categoriaExpanded = false
                        })
                    }
                }
            }

            // ── Servicio asociado (opcional) ────────────────────────────────────
            var servicioExpanded by remember { mutableStateOf(false) }
            val servicioSeleccionado = state.servicios.firstOrNull { it.id == state.servicioId }
            ExposedDropdownMenuBox(expanded = servicioExpanded, onExpandedChange = { servicioExpanded = it }) {
                OutlinedTextField(
                    value = servicioSeleccionado?.nombre ?: "Sin servicio asociado",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Servicio (opcional)") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = servicioExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = servicioExpanded, onDismissRequest = { servicioExpanded = false }) {
                    DropdownMenuItem(text = { Text("Sin servicio asociado") }, onClick = {
                        viewModel.setServicioId(null)
                        servicioExpanded = false
                    })
                    state.servicios.forEach { servicio ->
                        DropdownMenuItem(text = { Text(servicio.nombre) }, onClick = {
                            viewModel.setServicioId(servicio.id)
                            servicioExpanded = false
                        })
                    }
                    HorizontalDivider()
                    DropdownMenuItem(
                        text = { Text("+ Crear nuevo servicio", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary) },
                        onClick = {
                            servicioExpanded = false
                            viewModel.abrirDialogoNuevoServicio()
                        }
                    )
                }
            }

            if (state.mostrarDialogoServicio) {
                DialogoNuevoServicio(
                    tiposServicio = state.tiposServicio,
                    creando = state.creandoServicio,
                    error = state.errorCrearServicio,
                    onDismiss = viewModel::cerrarDialogoNuevoServicio,
                    onGuardar = { nombre, tipoServicioId -> viewModel.crearServicioInline(nombre, tipoServicioId) }
                )
            }

            // ── Ubicaciones (dónde se muestra el anuncio) ───────────────────────
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Ubicaciones", style = MaterialTheme.typography.labelLarge)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    UBICACIONES.forEach { (valor, etiqueta) ->
                        val seleccionada = valor in state.ubicaciones
                        FilterChip(
                            selected = seleccionada,
                            onClick = { viewModel.toggleUbicacion(valor) },
                            label = { Text(etiqueta) }
                        )
                    }
                }
                if ("banner" in state.ubicaciones) {
                    Text(
                        "El anuncio será de pago porque incluye Banner.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            OutlinedTextField(
                value = state.descuento,
                onValueChange = viewModel::setDescuento,
                label = { Text("Descuento (opcional, ej. 20%)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Caso minoritario: la mayoría de los anuncios agendan un servicio o no
            // tienen acción; el enlace externo queda oculto detrás de este switch.
            var usarEnlaceExterno by remember { mutableStateOf(state.urlBoton.isNotBlank()) }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Usar un enlace externo en vez de agendamiento", modifier = Modifier.weight(1f))
                Switch(checked = usarEnlaceExterno, onCheckedChange = { usarEnlaceExterno = it })
            }

            if (usarEnlaceExterno) {
                OutlinedTextField(
                    value = state.textoBoton,
                    onValueChange = viewModel::setTextoBoton,
                    label = { Text("Texto del botón") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = state.urlBoton,
                    onValueChange = viewModel::setUrlBoton,
                    label = { Text("URL del botón") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            var showDatePickerInicio by remember { mutableStateOf(false) }
            var showDatePickerFin by remember { mutableStateOf(false) }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FechaCard(
                    label = "Inicio",
                    valor = state.fechaInicio,
                    onClick = { showDatePickerInicio = true },
                    modifier = Modifier.weight(1f)
                )
                FechaCard(
                    label = "Fin",
                    valor = state.fechaFin,
                    onClick = { showDatePickerFin = true },
                    modifier = Modifier.weight(1f)
                )
            }

            if (showDatePickerInicio) {
                FechaPickerDialog(
                    fechaInicial = state.fechaInicio.toLocalDateOrNull(),
                    onDismiss = { showDatePickerInicio = false },
                    onConfirmar = { fecha ->
                        viewModel.setFechaInicio(fecha.toString())
                        showDatePickerInicio = false
                    }
                )
            }
            if (showDatePickerFin) {
                FechaPickerDialog(
                    fechaInicial = state.fechaFin.toLocalDateOrNull(),
                    onDismiss = { showDatePickerFin = false },
                    onConfirmar = { fecha ->
                        viewModel.setFechaFin(fecha.toString())
                        showDatePickerFin = false
                    }
                )
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
                    Text(if (state.esEdicion) "Guardar cambios" else "Crear anuncio")
                }
            }
        }
    }
}

private fun String.toLocalDateOrNull(): LocalDate? = runCatching { LocalDate.parse(this) }.getOrNull()

@Composable
private fun FechaCard(
    label: String,
    valor: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    valor.ifBlank { "Sin definir" },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Icon(
                Icons.Default.DateRange,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

/**
 * DatePickerState trabaja en milisegundos UTC (medianoche UTC del día elegido),
 * no en la zona horaria del dispositivo. Usar ZoneOffset.UTC en ambas direcciones
 * evita que la fecha se corra un día en zonas horarias detrás de UTC (como Colombia).
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FechaPickerDialog(
    fechaInicial: LocalDate?,
    onDismiss: () -> Unit,
    onConfirmar: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = (fechaInicial ?: LocalDate.now())
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        onConfirmar(Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate())
                    } else {
                        onDismiss()
                    }
                }
            ) { Text("Aceptar") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

/**
 * Crear un servicio sin salir del formulario de anuncio. El error de guardado
 * se muestra dentro del propio diálogo (no un snackbar) para no perderlo al
 * cerrarse; el diálogo solo se cierra cuando la creación realmente funciona.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DialogoNuevoServicio(
    tiposServicio: List<TipoServicio>,
    creando: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onGuardar: (nombre: String, tipoServicioId: Int?) -> Unit
) {
    var nombre by remember { mutableStateOf("") }
    var tipoServicioId by remember { mutableStateOf<Int?>(null) }
    var tipoExpanded by remember { mutableStateOf(false) }
    val tipoSeleccionado = tiposServicio.firstOrNull { it.id == tipoServicioId }

    AlertDialog(
        onDismissRequest = { if (!creando) onDismiss() },
        title = { Text("Nuevo servicio") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre *") },
                    singleLine = true,
                    enabled = !creando,
                    modifier = Modifier.fillMaxWidth()
                )
                ExposedDropdownMenuBox(
                    expanded = tipoExpanded,
                    onExpandedChange = { if (!creando) tipoExpanded = it }
                ) {
                    OutlinedTextField(
                        value = tipoSeleccionado?.nombre ?: "",
                        onValueChange = {},
                        readOnly = true,
                        enabled = !creando,
                        label = { Text("Tipo de servicio *") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoExpanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = tipoExpanded, onDismissRequest = { tipoExpanded = false }) {
                        if (tiposServicio.isEmpty()) {
                            DropdownMenuItem(text = { Text("No hay tipos disponibles") }, onClick = {})
                        }
                        tiposServicio.forEach { tipo ->
                            DropdownMenuItem(text = { Text(tipo.nombre) }, onClick = {
                                tipoServicioId = tipo.id
                                tipoExpanded = false
                            })
                        }
                    }
                }
                if (error != null) {
                    Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onGuardar(nombre, tipoServicioId) },
                enabled = !creando
            ) {
                if (creando) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = Color.White)
                } else {
                    Text("Guardar")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss, enabled = !creando) { Text("Cancelar") }
        }
    )
}
