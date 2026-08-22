package com.manuel.tutalleraunclic.ui.screens.establecimientos

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.manuel.tutalleraunclic.R
import com.manuel.tutalleraunclic.utils.fixImageUrl
import com.manuel.tutalleraunclic.viewmodel.EstablecimientoFormViewModel
import com.manuel.tutalleraunclic.viewmodel.UiEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import java.util.Locale

/** Ubicación por defecto cuando el establecimiento aún no tiene coordenadas (Popayán, Colombia). */
private val UBICACION_POR_DEFECTO = LatLng(2.44222, -76.61258)

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstablecimientoFormScreen(
    establecimientoId: Int?,
    onBack: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: EstablecimientoFormViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var erroresValidacion by remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) { viewModel.inicializar(establecimientoId) }

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
    ) { uri -> viewModel.setFotoUri(uri) }

    // ── Mapa embebido + búsqueda de dirección ───────────────────────────────
    val cameraPositionState = rememberCameraPositionState()
    var isLocating by remember { mutableStateOf(false) }
    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    // Se inicializa con la dirección ya guardada (modo edición) y se
    // actualiza cada vez que el usuario elige una ubicación nueva.
    var locationName by remember { mutableStateOf(state.direccion) }

    // Centra la cámara una sola vez, cuando terminan de cargarse los datos
    // (edición) o al entrar en modo creación, sin pelear con los taps del usuario.
    LaunchedEffect(state.isLoading) {
        if (!state.isLoading) {
            val lat = state.latitud.toDoubleOrNull()
            val lng = state.longitud.toDoubleOrNull()
            val inicial = if (lat != null && lng != null && lat != 0.0 && lng != 0.0) {
                LatLng(lat, lng)
            } else {
                UBICACION_POR_DEFECTO
            }
            cameraPositionState.position = CameraPosition.fromLatLngZoom(inicial, 14f)
        }
    }

    fun reverseGeocode(lat: Double, lng: Double) {
        scope.launch {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val resultados = withContext(Dispatchers.IO) {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocation(lat, lng, 1)
                }
                val direccion = resultados?.firstOrNull()?.getAddressLine(0)
                if (!direccion.isNullOrBlank()) {
                    locationName = direccion
                    viewModel.setDireccion(direccion)
                }
            } catch (_: Exception) {
            }
        }
    }

    fun moverAMiUbicacion() {
        isLocating = true
        scope.launch {
            try {
                val cancelToken = CancellationTokenSource()
                val fusedClient = LocationServices.getFusedLocationProviderClient(context)
                val location = suspendCancellableCoroutine<android.location.Location?> { cont ->
                    fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cancelToken.token)
                        .addOnSuccessListener { cont.resume(it) }
                        .addOnFailureListener { cont.resumeWithException(it) }
                    cont.invokeOnCancellation { cancelToken.cancel() }
                }
                if (location != null) {
                    val actual = LatLng(location.latitude, location.longitude)
                    viewModel.setLatitud(actual.latitude.toString())
                    viewModel.setLongitud(actual.longitude.toString())
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(actual, 15f))
                    reverseGeocode(actual.latitude, actual.longitude)
                }
            } catch (_: Exception) {
            } finally {
                isLocating = false
            }
        }
    }

    fun buscarDireccion() {
        if (searchQuery.isBlank()) return
        isSearching = true
        scope.launch {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())
                val resultados = withContext(Dispatchers.IO) {
                    @Suppress("DEPRECATION")
                    geocoder.getFromLocationName(searchQuery, 1)
                }
                val encontrada = resultados?.firstOrNull()
                if (encontrada != null) {
                    val latLng = LatLng(encontrada.latitude, encontrada.longitude)
                    viewModel.setLatitud(latLng.latitude.toString())
                    viewModel.setLongitud(latLng.longitude.toString())
                    locationName = encontrada.getAddressLine(0) ?: searchQuery
                    viewModel.setDireccion(locationName)
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
                    scope.launch { snackbarHostState.showSnackbar("Ubicación encontrada") }
                } else {
                    scope.launch { snackbarHostState.showSnackbar("Dirección no encontrada") }
                }
            } catch (_: Exception) {
                scope.launch { snackbarHostState.showSnackbar("Error al buscar la dirección") }
            } finally {
                isSearching = false
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> if (granted) moverAMiUbicacion() }

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
                        Text(if (state.esEdicion) "Editar establecimiento" else "Nuevo establecimiento", fontWeight = FontWeight.Bold)
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

            // ── Foto ─────────────────────────────────────────────────────────
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    val modelo: Any? = state.fotoUri ?: fixImageUrl(state.fotoUrlActual)
                    if (modelo != null) {
                        AsyncImage(
                            model = modelo,
                            contentDescription = "Foto del establecimiento",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Image, contentDescription = null)
                            Spacer(Modifier.height(4.dp))
                            Text("Toca para elegir una foto")
                        }
                    }
                }
                TextButton(onClick = { launcher.launch("image/*") }) {
                    Text(if (state.fotoUri != null || state.fotoUrlActual != null) "Cambiar foto" else "Elegir foto")
                }
            }

            OutlinedTextField(
                value = state.nombre,
                onValueChange = viewModel::setNombre,
                label = { Text("Nombre *") },
                modifier = Modifier.fillMaxWidth()
            )

            // ── Tipo ─────────────────────────────────────────────────────────
            var tipoExpanded by remember { mutableStateOf(false) }
            val tipoSeleccionado = state.tipos.firstOrNull { it.id == state.tipoId }
            ExposedDropdownMenuBox(expanded = tipoExpanded, onExpandedChange = { tipoExpanded = it }) {
                OutlinedTextField(
                    value = tipoSeleccionado?.nombre ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Tipo *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tipoExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = tipoExpanded, onDismissRequest = { tipoExpanded = false }) {
                    if (state.tipos.isEmpty()) {
                        DropdownMenuItem(text = { Text("No hay tipos disponibles") }, onClick = {})
                    }
                    state.tipos.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo.nombre) },
                            onClick = {
                                viewModel.setTipoId(tipo.id)
                                tipoExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = state.direccion,
                onValueChange = viewModel::setDireccion,
                label = { Text("Dirección") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.telefono,
                onValueChange = viewModel::setTelefono,
                label = { Text("Teléfono") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.descripcion,
                onValueChange = viewModel::setDescripcion,
                label = { Text("Descripción") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = state.horaApertura,
                    onValueChange = viewModel::setHoraApertura,
                    label = { Text("Hora apertura (HH:mm)") },
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.horaCierre,
                    onValueChange = viewModel::setHoraCierre,
                    label = { Text("Hora cierre (HH:mm)") },
                    modifier = Modifier.weight(1f)
                )
            }

            // ── Ubicación (mapa embebido) ───────────────────────────────────
            val establecimientoLatLng = remember(state.latitud, state.longitud) {
                val lat = state.latitud.toDoubleOrNull()
                val lng = state.longitud.toDoubleOrNull()
                if (lat != null && lng != null && lat != 0.0 && lng != 0.0) LatLng(lat, lng) else UBICACION_POR_DEFECTO
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column {
                    Text(
                        "Ubicación del establecimiento",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(12.dp)
                    )

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Buscar dirección") },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Limpiar")
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { buscarDireccion() })
                    )

                    Button(
                        onClick = { buscarDireccion() },
                        enabled = !isSearching && searchQuery.isNotBlank(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .height(40.dp)
                    ) {
                        if (isSearching) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                        } else {
                            Text("Buscar en mapa")
                        }
                    }

                    GoogleMap(
                        modifier = Modifier.fillMaxWidth().height(300.dp),
                        cameraPositionState = cameraPositionState,
                        uiSettings = MapUiSettings(zoomControlsEnabled = true, mapToolbarEnabled = false),
                        onMapClick = { latLng ->
                            viewModel.setLatitud(latLng.latitude.toString())
                            viewModel.setLongitud(latLng.longitude.toString())
                            reverseGeocode(latLng.latitude, latLng.longitude)
                        }
                    ) {
                        Marker(
                            state = MarkerState(position = establecimientoLatLng),
                            title = state.nombre.ifBlank { "Ubicación del establecimiento" },
                            snippet = locationName.ifBlank { null }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("📍 Ubicación seleccionada", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(
                                locationName.ifBlank { "Toca el mapa o busca una dirección" },
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Button(
                            onClick = {
                                val granted = ContextCompat.checkSelfPermission(
                                    context, Manifest.permission.ACCESS_FINE_LOCATION
                                ) == PackageManager.PERMISSION_GRANTED
                                if (granted) {
                                    moverAMiUbicacion()
                                } else {
                                    locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                            },
                            modifier = Modifier.height(40.dp)
                        ) {
                            if (isLocating) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = Color.White)
                            } else {
                                Icon(Icons.Default.MyLocation, contentDescription = "Mi ubicación", tint = Color.White)
                            }
                        }
                    }

                    Text(
                        "💡 Toca el mapa para marcar la ubicación, busca una dirección o usa tu ubicación actual",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    erroresValidacion = viewModel.validar()
                    if (erroresValidacion.isEmpty()) viewModel.guardar()
                },
                enabled = !state.isSaving,
                modifier = Modifier.fillMaxWidth().height(48.dp)
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                } else {
                    Text(if (state.esEdicion) "Guardar cambios" else "Crear establecimiento")
                }
            }

            if (erroresValidacion.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "⚠️ Errores en el formulario:",
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFC62828)
                        )
                        erroresValidacion.forEach { error ->
                            Text("• $error", color = Color(0xFFC62828), fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}
