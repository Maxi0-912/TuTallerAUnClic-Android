package com.manuel.tutalleraunclic.ui.screens.establecimientos

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.manuel.tutalleraunclic.core.navigation.Routes
import com.manuel.tutalleraunclic.ui.components.EstablecimientoCardPro
import com.manuel.tutalleraunclic.viewmodel.EstablecimientoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaEstablecimientosScreen(
    navController: NavController,
    viewModel: EstablecimientoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    // Request location on first launch
    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) obtenerUbicacion(context, viewModel)
    }

    LaunchedEffect(Unit) {
        val fineGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (fineGranted) obtenerUbicacion(context, viewModel)
        else locationLauncher.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )

        // Consume any one-shot message stored after login (e.g. empresa notice)
        viewModel.checkAndEmitPendingMessage()
    }

    // Show pending messages (one-shot, consumed once from storage)
    LaunchedEffect(Unit) {
        viewModel.pendingMessage.collect { msg ->
            snackbarHostState.showSnackbar(
                message  = msg,
                duration = SnackbarDuration.Long
            )
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Talleres y Lavaderos") })
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                viewModel.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                viewModel.error != null && viewModel.lista.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No se pudieron cargar los establecimientos")
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.cargarEstablecimientos() }) {
                            Text("Reintentar")
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(viewModel.lista, key = { it.id }) { item ->
                            EstablecimientoCardPro(
                                establecimiento = item,
                                onClick = {
                                    navController.navigate(Routes.detalle(item.id.toString()))
                                },
                                onAgendarClick = {
                                    navController.navigate(Routes.cita(item.id, 1))
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun obtenerUbicacion(context: android.content.Context, viewModel: EstablecimientoViewModel) {
    try {
        LocationServices.getFusedLocationProviderClient(context)
            .lastLocation
            .addOnSuccessListener { location ->
                if (location != null) viewModel.actualizarUbicacion(location.latitude, location.longitude)
            }
    } catch (_: SecurityException) {}
}
