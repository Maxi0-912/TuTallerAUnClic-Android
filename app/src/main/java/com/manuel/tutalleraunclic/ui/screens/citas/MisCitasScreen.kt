package com.manuel.tutalleraunclic.ui.screens.citas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.manuel.tutalleraunclic.ui.components.CitaItem
import com.manuel.tutalleraunclic.viewmodel.CitaListViewModel
import com.manuel.tutalleraunclic.viewmodel.CitasState
import androidx.compose.material3.TopAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MisCitasScreen(
    viewModel: CitaListViewModel = hiltViewModel()
) {

    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.obtenerCitas()
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Mis citas") })
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            when (state) {

                is CitasState.Loading -> {
                    CircularProgressIndicator()
                }

                is CitasState.Error -> {
                    Text("Error al cargar citas")
                }

                is CitasState.Success -> {

                    val citas = (state as CitasState.Success).citas

                    if (citas.isEmpty()) {

                        // 🧊 EMPTY STATE PRO
                        Text(
                            text = "No tienes citas aún 🚗",
                            modifier = Modifier.padding(16.dp)
                        )

                    } else {

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.padding(16.dp)
                        ) {

                            items(citas) { cita ->
                                CitaItem(cita)
                            }
                        }
                    }
                }
            }
        }
    }
}