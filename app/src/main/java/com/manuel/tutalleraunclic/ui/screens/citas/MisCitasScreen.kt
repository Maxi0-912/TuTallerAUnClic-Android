package com.manuel.tutalleraunclic.ui.screens.citas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.manuel.tutalleraunclic.ui.components.CitaItem
import com.manuel.tutalleraunclic.viewmodel.MisCitasViewModel
import com.manuel.tutalleraunclic.viewmodel.CitasState
import kotlinx.coroutines.launch
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun MisCitasScreen(
    viewModel: MisCitasViewModel = hiltViewModel(),
    navController: NavController
){

    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // 🔥 Estado global correcto
    var citaAEliminar by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.obtenerCitas()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis citas") }
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

            // 🔥 DIALOG GLOBAL (correcto)
            if (citaAEliminar != null) {
                AlertDialog(
                    onDismissRequest = { citaAEliminar = null },

                    confirmButton = {
                        Button(onClick = {
                            viewModel.eliminarCita(citaAEliminar!!)

                            scope.launch {
                                snackbarHostState.showSnackbar("Cita eliminada correctamente")
                            }

                            citaAEliminar = null
                        }) {
                            Text("Sí, eliminar")
                        }
                    },

                    dismissButton = {
                        OutlinedButton(
                            onClick = { citaAEliminar = null }
                        ) {
                            Text("Cancelar")
                        }
                    },

                    title = { Text("Eliminar cita") },
                    text = { Text("¿Seguro que deseas eliminar esta cita?") }
                )
            }

            when (state) {

                is CitasState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is CitasState.Error -> {
                    Text(
                        text = "Error al cargar citas",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is CitasState.Success -> {

                    val citas = (state as CitasState.Success).citas

                    if (citas.isEmpty()) {

                        // 🧊 EMPTY STATE PRO
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
                                key = { it.id } // 🔥 IMPORTANTE (performance + animaciones)
                            ) { cita ->

                                CitaItem(
                                    cita = cita,

                                    onDelete = {
                                        citaAEliminar = cita.id
                                    },

                                    onEdit = {
                                        navController.navigate("editar_cita/${cita.id}")
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