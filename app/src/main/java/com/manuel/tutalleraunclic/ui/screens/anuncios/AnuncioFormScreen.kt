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
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.manuel.tutalleraunclic.R
import com.manuel.tutalleraunclic.utils.fixImageUrl
import com.manuel.tutalleraunclic.viewmodel.AnuncioFormViewModel
import com.manuel.tutalleraunclic.viewmodel.UiEvent
import kotlinx.coroutines.launch

private val TIPOS = listOf("imagen" to "Solo imagen", "imagen_texto" to "Imagen con texto", "imagen_boton" to "Imagen con botón")
private val CATEGORIAS = listOf("banner" to "Banner (inicio)", "oferta" to "Oferta")

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

    LaunchedEffect(state.guardadoExitoso) {
        if (state.guardadoExitoso) onSuccess()
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

            OutlinedTextField(
                value = state.descuento,
                onValueChange = viewModel::setDescuento,
                label = { Text("Descuento (opcional, ej. 20%)") },
                modifier = Modifier.fillMaxWidth()
            )

            if (state.tipo == "imagen_boton") {
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

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = state.fechaInicio,
                    onValueChange = viewModel::setFechaInicio,
                    label = { Text("Inicio (AAAA-MM-DD)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.fechaFin,
                    onValueChange = viewModel::setFechaFin,
                    label = { Text("Fin (AAAA-MM-DD)") },
                    modifier = Modifier.weight(1f)
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
