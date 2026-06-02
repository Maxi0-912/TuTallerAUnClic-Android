package com.manuel.tutalleraunclic.ui.screens.citas

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBar
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
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.manuel.tutalleraunclic.R
import com.manuel.tutalleraunclic.core.navigation.Routes
import com.manuel.tutalleraunclic.data.model.response.CitaResponse
import com.manuel.tutalleraunclic.ui.components.AppAlertDialog
import com.manuel.tutalleraunclic.ui.components.CitaItem
import com.manuel.tutalleraunclic.viewmodel.CitasState
import com.manuel.tutalleraunclic.viewmodel.MisCitasViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisCitasScreen(
    viewModel: MisCitasViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var citaAEliminar by remember { mutableStateOf<Int?>(null) }
    var citaDetalle   by remember { mutableStateOf<CitaResponse?>(null) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(navBackStackEntry) {
        viewModel.obtenerCitas()
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.obtenerCitas()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
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
                        Text("Mis citas", fontWeight = FontWeight.Bold)
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            if (citaAEliminar != null) {
                AppAlertDialog(
                    onDismissRequest = { citaAEliminar = null },
                    confirmButton = {
                        Button(onClick = {
                            viewModel.eliminarCita(citaAEliminar!!)
                            scope.launch {
                                snackbarHostState.showSnackbar("Cita cancelada")
                            }
                            citaAEliminar = null
                        }) {
                            Text("Sí, cancelar")
                        }
                    },
                    dismissButton = {
                        OutlinedButton(onClick = { citaAEliminar = null }) {
                            Text("Volver")
                        }
                    },
                    title = { Text("Cancelar cita") },
                    text = { Text("¿Seguro que deseas cancelar esta cita?") }
                )
            }

            citaDetalle?.let { cita ->
                DetalleCitaDialog(
                    cita = cita,
                    onClose = { citaDetalle = null },
                    onCalificar = {
                        citaDetalle = null
                        navController.navigate(Routes.resena(cita.id, 0))
                    }
                )
            }

            when (state) {

                is CitasState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is CitasState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Error al cargar citas")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.obtenerCitas() }) {
                            Text("Reintentar")
                        }
                    }
                }

                is CitasState.Success -> {
                    val citas = (state as CitasState.Success).citas

                    if (citas.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🚗 No tienes citas aún",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Agenda tu primera cita y aparecerá aquí",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(16.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(
                                items = citas,
                                key = { it.id }
                            ) { cita ->
                                CitaItem(
                                    cita         = cita,
                                    onDelete     = { citaAEliminar = cita.id },
                                    onEdit       = { navController.navigate("editar_cita/${cita.id}") },
                                    onCalificar  = { navController.navigate(Routes.resena(cita.id, 0)) },
                                    onVerDetalle = { citaDetalle = cita }
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
fun DetalleCitaDialog(
    cita: CitaResponse,
    onClose: () -> Unit,
    onCalificar: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onClose,
        confirmButton = { },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = cita.establecimiento_nombre ?: "",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Text(
                            text = "Cita #${cita.id}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                    TextButton(onClick = onClose) { Text("Cerrar") }
                }

                HorizontalDivider()

                // Info básica
                InfoDetalle("📅 Fecha", cita.fecha)
                InfoDetalle("⏰ Hora", cita.hora.take(5))
                cita.vehiculo_placa?.let { InfoDetalle("🚗 Vehículo", it) }

                val servicio = cita.servicio_nombre ?: cita.servicio_texto
                if (!servicio.isNullOrBlank()) {
                    InfoDetalle("🔧 Servicio", servicio)
                }

                // Descripción del cliente
                if (!cita.descripcion.isNullOrBlank()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "Tu solicitud",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                cita.descripcion ?: "",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }

                // Comentario de la empresa
                if (!cita.comentario_empresa.isNullOrBlank()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF1B5E20).copy(alpha = 0.15f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                "💬 Comentario del taller",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF4CAF50)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                cita.comentario_empresa ?: "",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                } else {
                    Text(
                        "El taller aún no ha agregado comentarios",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )
                }

                // Botón calificar si no tiene reseña
                if (!cita.tiene_resena) {
                    Button(
                        onClick = onCalificar,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF57C00)
                        )
                    ) {
                        Text("⭐ Calificar este servicio")
                    }
                } else {
                    Text(
                        "✓ Ya calificaste este servicio",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    )
}

@Composable
private fun InfoDetalle(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium)
        )
    }
}
