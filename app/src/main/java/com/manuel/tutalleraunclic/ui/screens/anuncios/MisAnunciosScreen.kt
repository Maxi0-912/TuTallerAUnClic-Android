package com.manuel.tutalleraunclic.ui.screens.anuncios

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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.platform.LocalLifecycleOwner
import coil.compose.AsyncImage
import com.manuel.tutalleraunclic.R
import com.manuel.tutalleraunclic.data.model.entity.Anuncio
import com.manuel.tutalleraunclic.ui.components.AppAlertDialog
import com.manuel.tutalleraunclic.utils.fixImageUrl
import com.manuel.tutalleraunclic.viewmodel.MisAnunciosViewModel
import com.manuel.tutalleraunclic.viewmodel.UiEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisAnunciosScreen(
    onBack: () -> Unit,
    onCrear: () -> Unit,
    onEditar: (Int) -> Unit,
    viewModel: MisAnunciosViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var anuncioAEliminar by remember { mutableStateOf<Int?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) { viewModel.cargarMisAnuncios() }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.cargarMisAnuncios()
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
                        Text("Mis anuncios", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onCrear, icon = {
                Icon(Icons.Default.Add, contentDescription = null)
            }, text = { Text("Nuevo anuncio") })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            anuncioAEliminar?.let { id ->
                AppAlertDialog(
                    onDismissRequest = { anuncioAEliminar = null },
                    confirmButton = {
                        Button(onClick = {
                            viewModel.eliminarAnuncio(id)
                            anuncioAEliminar = null
                        }) { Text("Sí, borrar") }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { anuncioAEliminar = null }) { Text("Cancelar") }
                    },
                    title = { Text("Borrar anuncio") },
                    text = { Text("¿Seguro que deseas borrar este anuncio? Esta acción no se puede deshacer.") }
                )
            }

            when {
                state.isLoading && state.anuncios.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.anuncios.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "Todavía no tienes anuncios",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                "Crea uno para promocionar tu establecimiento",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.anuncios, key = { it.id }) { anuncio ->
                            AnuncioCard(
                                anuncio = anuncio,
                                onEditar = { onEditar(anuncio.id) },
                                onEliminar = { anuncioAEliminar = anuncio.id }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AnuncioCard(
    anuncio: Anuncio,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column {
            Box {
                AsyncImage(
                    model = fixImageUrl(anuncio.imagen_url),
                    contentDescription = anuncio.titulo,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(140.dp)
                )
                EstadoBadge(
                    estado = anuncio.estado,
                    modifier = Modifier.align(Alignment.TopEnd).padding(10.dp)
                )
            }
            Column(modifier = Modifier.padding(14.dp)) {
                Text(anuncio.titulo, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                if (!anuncio.establecimiento_nombre.isNullOrBlank()) {
                    Text(
                        anuncio.establecimiento_nombre,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                if (anuncio.estado?.lowercase() == "rechazado" && !anuncio.motivo_rechazo.isNullOrBlank()) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "Motivo: ${anuncio.motivo_rechazo}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                if (anuncio.es_pago == true) {
                    Spacer(Modifier.height(6.dp))
                    Text(
                        if (anuncio.pagado == true) "Pago confirmado" else "Pendiente de pago",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (anuncio.pagado == true) Color(0xFF2E7D32) else Color(0xFFE65100)
                    )
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(onClick = onEditar, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Editar")
                    }
                    OutlinedButton(
                        onClick = onEliminar,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Borrar")
                    }
                }
            }
        }
    }
}

@Composable
private fun EstadoBadge(estado: String?, modifier: Modifier = Modifier) {
    val (texto, color) = when (estado?.lowercase()) {
        "aprobado"  -> "Aprobado" to Color(0xFF2E7D32)
        "rechazado" -> "Rechazado" to Color(0xFFC62828)
        else        -> "Pendiente" to Color(0xFFE65100)
    }
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(texto, color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
    }
}
