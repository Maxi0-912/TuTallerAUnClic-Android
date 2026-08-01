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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import com.manuel.tutalleraunclic.R
import com.manuel.tutalleraunclic.data.model.response.EstablecimientoEmpresaResponse
import com.manuel.tutalleraunclic.ui.components.AppAlertDialog
import com.manuel.tutalleraunclic.utils.fixImageUrl
import com.manuel.tutalleraunclic.viewmodel.MisEstablecimientosViewModel
import com.manuel.tutalleraunclic.viewmodel.UiEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisEstablecimientosScreen(
    onBack: () -> Unit,
    onCrear: () -> Unit,
    onEditar: (Int) -> Unit,
    viewModel: MisEstablecimientosViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var establecimientoAEliminar by remember { mutableStateOf<Int?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) { viewModel.cargarEstablecimientos() }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.cargarEstablecimientos()
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
                        Text("Mis establecimientos", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onCrear, icon = {
                Icon(Icons.Default.Add, contentDescription = null)
            }, text = { Text("Nuevo establecimiento") })
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { padding ->

        Box(modifier = Modifier.padding(padding).fillMaxSize()) {

            establecimientoAEliminar?.let { id ->
                AppAlertDialog(
                    onDismissRequest = { establecimientoAEliminar = null },
                    confirmButton = {
                        Button(onClick = {
                            viewModel.eliminarEstablecimiento(id)
                            establecimientoAEliminar = null
                        }) { Text("Sí, eliminar") }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { establecimientoAEliminar = null }) { Text("Cancelar") }
                    },
                    title = { Text("Eliminar establecimiento") },
                    text = { Text("Se eliminarán todas las citas y servicios asociados. Esta acción no se puede deshacer.") }
                )
            }

            when {
                state.isLoading && state.establecimientos.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.establecimientos.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("🔧", style = MaterialTheme.typography.displaySmall)
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "No tienes establecimientos registrados",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                            Spacer(Modifier.height(16.dp))
                            Button(onClick = onCrear) { Text("Registrar mi primer establecimiento") }
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp, 16.dp, 16.dp, 96.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.establecimientos, key = { it.id }) { est ->
                            EstablecimientoEmpresaCard(
                                establecimiento = est,
                                onEditar = { onEditar(est.id) },
                                onEliminar = { establecimientoAEliminar = est.id }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EstablecimientoEmpresaCard(
    establecimiento: EstablecimientoEmpresaResponse,
    onEditar: () -> Unit,
    onEliminar: () -> Unit
) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column {
            Box {
                Box(
                    modifier = Modifier.fillMaxWidth().height(140.dp).background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (!establecimiento.foto_url.isNullOrBlank()) {
                        AsyncImage(
                            model = fixImageUrl(establecimiento.foto_url),
                            contentDescription = establecimiento.nombre,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(
                            if (establecimiento.tipo_nombre?.lowercase() == "taller") "🔧" else "💧",
                            style = MaterialTheme.typography.displayMedium
                        )
                    }
                }
                if (!establecimiento.tipo_nombre.isNullOrBlank()) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(10.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.9f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(establecimiento.tipo_nombre, color = Color.White, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Column(modifier = Modifier.padding(14.dp)) {
                Text(establecimiento.nombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)

                if (!establecimiento.direccion.isNullOrBlank()) {
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                        Text(
                            establecimiento.direccion,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                if (!establecimiento.hora_apertura.isNullOrBlank() && !establecimiento.hora_cierre.isNullOrBlank()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "🕐 ${establecimiento.hora_apertura.take(5)} – ${establecimiento.hora_cierre.take(5)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                establecimiento.promedio_calificacion?.let { calif ->
                    Spacer(Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(2.dp)) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFC107), modifier = Modifier.size(14.dp))
                        Text("%.1f".format(calif), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
                    }
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
                        Text("Eliminar")
                    }
                }
            }
        }
    }
}
