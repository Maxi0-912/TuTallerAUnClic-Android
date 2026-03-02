package com.manuel.tutalleraunclic.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.manuel.tutalleraunclic.utils.TokenManager
import com.manuel.tutalleraunclic.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {

    val viewModel: HomeViewModel = viewModel()
    val establecimientos = viewModel.establecimientos.value

    LaunchedEffect(Unit) {
        viewModel.cargarEstablecimientos()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Establecimientos") },
                actions = {
                    IconButton(onClick = {
                        TokenManager.clearToken(navController.context)
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    }) {
                        Icon(Icons.Default.ExitToApp, contentDescription = "Cerrar sesión")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("crear_establecimiento")
                }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar")
            }
        }
    ) { paddingValues ->

        if (establecimientos.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Text("No hay establecimientos disponibles")
            }

        } else {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                items(establecimientos) { establecimiento ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {

                            Text(
                                text = establecimiento.nombre,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(text = establecimiento.direccion)

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(text = "Tel: ${establecimiento.telefono}")
                        }
                    }
                }
            }
        }
    }
}