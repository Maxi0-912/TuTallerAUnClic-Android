package com.manuel.tutalleraunclic.ui.screens.citas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.manuel.tutalleraunclic.ui.components.CitaItem
import com.manuel.tutalleraunclic.viewmodel.MisCitasViewModel
import com.manuel.tutalleraunclic.viewmodel.CitasState
import kotlinx.coroutines.launch
import androidx.navigation.NavController
import com.manuel.tutalleraunclic.core.navigation.Routes

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
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.obtenerCitas()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis citas") })
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
                AlertDialog(
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
                                    cita = cita,
                                    onDelete = { citaAEliminar = cita.id },
                                    onEdit = {
                                        navController.navigate("editar_cita/${cita.id}")
                                    },
                                    onCalificar = {
                                        navController.navigate(
                                            Routes.resena(cita.id, 0)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
