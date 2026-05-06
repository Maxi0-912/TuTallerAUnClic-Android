package com.manuel.tutalleraunclic.ui.screens.mapa

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Path
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import com.manuel.tutalleraunclic.core.navigation.Routes
import com.manuel.tutalleraunclic.data.model.EstablecimientoUI
import com.manuel.tutalleraunclic.data.model.TipoEstablecimiento
import com.manuel.tutalleraunclic.ui.components.BotonVolverInicio
import com.manuel.tutalleraunclic.viewmodel.EstablecimientoViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// ── Paleta ───────────────────────────────────────────────────────────────────
private val ColorTaller    = Color(0xFFEF4444)
private val ColorLavadero  = Color(0xFF3B82F6)
private val ColorUbicacion = Color(0xFF4CAF50)
private val ColorEstrella  = Color(0xFFFACC15)
private val ColorGris      = Color(0xFFD1D5DB)

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("MissingPermission")
@Composable
fun MapScreen(
    navController: NavController,
    viewModel: EstablecimientoViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope   = rememberCoroutineScope()

    val medellin = LatLng(6.2442, -75.5812)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(medellin, 13f)
    }

    // ── Estado ───────────────────────────────────────────────────────────────
    var userLocation            by remember { mutableStateOf<LatLng?>(null) }
    var selectedEstablecimiento by remember { mutableStateOf<EstablecimientoUI?>(null) }
    var showMiUbicacion         by remember { mutableStateOf(false) }
    var filtroActivo            by remember { mutableStateOf<TipoEstablecimiento?>(null) }
    var showListaSheet          by remember { mutableStateOf(false) }

    val detalleSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val listaSheetState   = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Lista filtrada: se recalcula cuando cambia la lista o el filtro
    val listaFiltrada = remember(viewModel.lista, filtroActivo) {
        when (filtroActivo) {
            is TipoEstablecimiento.Taller   -> viewModel.lista.filter { it.tipo is TipoEstablecimiento.Taller }
            is TipoEstablecimiento.Lavadero -> viewModel.lista.filter { it.tipo is TipoEstablecimiento.Lavadero }
            else                            -> viewModel.lista
        }
    }

    // Bitmaps de marcadores — recalcular solo cuando cambia la lista completa
    val iconCache = remember(viewModel.lista) {
        viewModel.lista.associate { est ->
            val hue = when (est.tipo) {
                is TipoEstablecimiento.Taller   -> BitmapDescriptorFactory.HUE_RED
                is TipoEstablecimiento.Lavadero -> BitmapDescriptorFactory.HUE_AZURE
                is TipoEstablecimiento.Otro     -> BitmapDescriptorFactory.HUE_YELLOW
            }
            est.id to Pair(pinBitmap(context, hue, false), pinBitmap(context, hue, true))
        }
    }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    fun fetchLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val latLng = LatLng(location.latitude, location.longitude)
                userLocation = latLng
                scope.launch {
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
                viewModel.actualizarUbicacion(location.latitude, location.longitude)
            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) fetchLocation()
    }

    LaunchedEffect(Unit) {
        val fine   = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED) {
            fetchLocation()
        } else {
            permissionLauncher.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }
    }

    // Centra cámara en marcador seleccionado
    LaunchedEffect(selectedEstablecimiento) {
        selectedEstablecimiento?.let { est ->
            val lat = if (est.latitud  != 0.0) est.latitud  else 6.2442  + est.id * 0.002
            val lng = if (est.longitud != 0.0) est.longitud else -75.5812 + est.id * 0.001
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 16f), 400)
        }
    }

    // Cuando cambia el filtro, limpiar selección
    LaunchedEffect(filtroActivo) {
        selectedEstablecimiento = null
    }

    // Auto-dismiss del popup de ubicación
    LaunchedEffect(showMiUbicacion) {
        if (showMiUbicacion) { delay(2500); showMiUbicacion = false }
    }

    fun navegarADetalle(est: EstablecimientoUI) {
        val route = when (est.tipo) {
            is TipoEstablecimiento.Taller   -> Routes.taller(est.id)
            is TipoEstablecimiento.Lavadero -> Routes.lavadero(est.id)
            is TipoEstablecimiento.Otro     -> Routes.detalle(est.id.toString())
        }
        navController.navigate(route)
    }

    // ── Layout ───────────────────────────────────────────────────────────────
    Box(modifier = Modifier.fillMaxSize()) {

        GoogleMap(
            modifier            = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings          = MapUiSettings(
                zoomControlsEnabled     = false,
                myLocationButtonEnabled = false
            )
        ) {
            // Marcador de ubicación del usuario
            userLocation?.let { loc ->
                Marker(
                    state   = MarkerState(position = loc),
                    title   = "Tu ubicación",
                    icon    = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN),
                    zIndex  = 10f,
                    onClick = { _ -> showMiUbicacion = true; true }
                )
            }

            // Marcadores filtrados de establecimientos
            listaFiltrada.forEach { est ->
                val lat = if (est.latitud  != 0.0) est.latitud  else 6.2442  + est.id * 0.002
                val lng = if (est.longitud != 0.0) est.longitud else -75.5812 + est.id * 0.001

                val isSelected = selectedEstablecimiento?.id == est.id
                val (normalIcon, bigIcon) = iconCache[est.id]
                    ?: (BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED) to
                        BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))

                Marker(
                    state   = MarkerState(position = LatLng(lat, lng)),
                    title   = est.nombre,
                    icon    = if (isSelected) bigIcon else normalIcon,
                    zIndex  = if (isSelected) 5f else 0f,
                    onClick = { _ -> selectedEstablecimiento = est; true }
                )
            }
        }

        // ── FAB mi ubicación ────────────────────────────────────────────────
        FloatingActionButton(
            onClick = {
                val target = userLocation ?: medellin
                val zoom   = if (userLocation != null) 15f else 13f
                scope.launch {
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(target, zoom))
                }
                if (userLocation == null) {
                    val fine = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    if (fine == PackageManager.PERMISSION_GRANTED) fetchLocation()
                    else permissionLauncher.launch(arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ))
                }
            },
            modifier       = Modifier.align(Alignment.TopEnd).padding(16.dp),
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor   = if (userLocation != null) MaterialTheme.colorScheme.primary
                             else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "Centrar en mi ubicación")
        }

        // ── Botón volver al inicio ───────────────────────────────────────────
        Surface(
            modifier       = Modifier.align(Alignment.TopEnd).padding(top = 80.dp, end = 16.dp),
            shape          = MaterialTheme.shapes.small,
            color          = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            BotonVolverInicio(navController)
        }

        // ── Indicador de carga ───────────────────────────────────────────────
        if (viewModel.isLoading) {
            Card(
                modifier = Modifier.align(Alignment.TopCenter).padding(top = 16.dp),
                shape    = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier              = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                    Text("Cargando…", style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // ── Leyenda interactiva (filtro) ─────────────────────────────────────
        Card(
            modifier = Modifier.align(Alignment.TopStart).padding(16.dp),
            shape    = RoundedCornerShape(12.dp),
            colors   = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
            ),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp)) {
                FiltroItem(
                    color  = ColorTaller,
                    label  = "Taller",
                    activo = filtroActivo is TipoEstablecimiento.Taller,
                    onClick = {
                        if (filtroActivo is TipoEstablecimiento.Taller) {
                            filtroActivo   = null
                            showListaSheet = false
                        } else {
                            filtroActivo   = TipoEstablecimiento.Taller
                            showListaSheet = true
                        }
                    }
                )
                Spacer(Modifier.height(2.dp))
                FiltroItem(
                    color  = ColorLavadero,
                    label  = "Lavadero",
                    activo = filtroActivo is TipoEstablecimiento.Lavadero,
                    onClick = {
                        if (filtroActivo is TipoEstablecimiento.Lavadero) {
                            filtroActivo   = null
                            showListaSheet = false
                        } else {
                            filtroActivo   = TipoEstablecimiento.Lavadero
                            showListaSheet = true
                        }
                    }
                )
                Spacer(Modifier.height(2.dp))
                FiltroItem(
                    color  = ColorUbicacion,
                    label  = "Tú",
                    activo = false,
                    onClick = {
                        val target = userLocation ?: medellin
                        scope.launch {
                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(target, 15f)
                            )
                        }
                    }
                )
            }
        }

        // ── Popup "Tu ubicación actual" ──────────────────────────────────────
        if (showMiUbicacion) {
            Card(
                modifier  = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 72.dp),
                shape     = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(6.dp)
            ) {
                Row(
                    modifier              = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment     = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint     = ColorUbicacion,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        "Tu ubicación actual",
                        style      = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }

    // ── BottomSheet: lista de establecimientos por tipo ───────────────────────
    if (showListaSheet) {
        filtroActivo?.let { tipo ->
            ModalBottomSheet(
                onDismissRequest = { showListaSheet = false; filtroActivo = null },
                sheetState       = listaSheetState,
                containerColor   = MaterialTheme.colorScheme.surface,
                tonalElevation   = 0.dp
            ) {
                ListaEstablecimientosSheet(
                    tipo      = tipo,
                    lista     = listaFiltrada,
                    onSeleccionar = { est ->
                        scope.launch {
                            listaSheetState.hide()
                            showListaSheet = false
                            selectedEstablecimiento = est
                        }
                    },
                    onVerDetalle = { est ->
                        scope.launch {
                            listaSheetState.hide()
                            showListaSheet = false
                            navegarADetalle(est)
                        }
                    },
                    onDismiss = {
                        scope.launch {
                            listaSheetState.hide()
                            showListaSheet = false
                            filtroActivo = null
                        }
                    }
                )
            }
        }
    }

    // ── BottomSheet: detalle de establecimiento seleccionado ─────────────────
    selectedEstablecimiento?.let { est ->
        ModalBottomSheet(
            onDismissRequest = { selectedEstablecimiento = null },
            sheetState       = detalleSheetState,
            containerColor   = MaterialTheme.colorScheme.surface,
            tonalElevation   = 0.dp
        ) {
            EstablecimientoDetalleSheet(
                est          = est,
                onVerDetalle = {
                    scope.launch {
                        detalleSheetState.hide()
                        selectedEstablecimiento = null
                        navegarADetalle(est)
                    }
                },
                onDismiss = {
                    scope.launch {
                        detalleSheetState.hide()
                        selectedEstablecimiento = null
                    }
                }
            )
        }
    }
}

// ── BottomSheet: lista ────────────────────────────────────────────────────────

@Composable
private fun ListaEstablecimientosSheet(
    tipo: TipoEstablecimiento,
    lista: List<EstablecimientoUI>,
    onSeleccionar: (EstablecimientoUI) -> Unit,
    onVerDetalle: (EstablecimientoUI) -> Unit,
    onDismiss: () -> Unit
) {
    val titulo = when (tipo) {
        is TipoEstablecimiento.Taller   -> "Talleres"
        is TipoEstablecimiento.Lavadero -> "Lavaderos"
        else                            -> "Establecimientos"
    }
    val badgeColor = when (tipo) {
        is TipoEstablecimiento.Taller   -> ColorTaller
        is TipoEstablecimiento.Lavadero -> ColorLavadero
        else                            -> ColorGris
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Cabecera
        Row(
            modifier              = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(badgeColor, RoundedCornerShape(50))
                )
                Text(
                    text       = titulo,
                    style      = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = badgeColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text     = "${lista.size}",
                        color    = badgeColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Cerrar")
            }
        }

        HorizontalDivider()

        if (lista.isEmpty()) {
            Box(
                modifier          = Modifier.fillMaxWidth().height(120.dp),
                contentAlignment  = Alignment.Center
            ) {
                Text(
                    "No hay $titulo disponibles",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
            ) {
                items(lista, key = { it.id }) { est ->
                    ListaItem(
                        est           = est,
                        onCentrar     = { onSeleccionar(est) },
                        onVerDetalle  = { onVerDetalle(est) }
                    )
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        color    = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))
    }
}

@Composable
private fun ListaItem(
    est: EstablecimientoUI,
    onCentrar: () -> Unit,
    onVerDetalle: () -> Unit
) {
    Row(
        modifier              = Modifier
            .fillMaxWidth()
            .clickable { onCentrar() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text       = est.nombre,
                style      = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint     = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    modifier = Modifier.size(12.dp)
                )
                Text(
                    text     = est.direccion,
                    style    = MaterialTheme.typography.bodySmall,
                    color    = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Row(
                verticalAlignment     = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = null,
                    tint     = ColorEstrella,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    "%.1f".format(est.rating),
                    style      = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            est.distanciaKm?.let { km ->
                Text(
                    "%.1f km".format(km),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                )
            }
        }

        // Botón "Ver detalle" compacto
        FilledTonalButton(
            onClick             = onVerDetalle,
            contentPadding      = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
            modifier            = Modifier.height(32.dp)
        ) {
            Icon(Icons.Default.ChevronRight, contentDescription = null, modifier = Modifier.size(14.dp))
        }
    }
}

// ── BottomSheet: detalle de un establecimiento ────────────────────────────────

@Composable
private fun EstablecimientoDetalleSheet(
    est: EstablecimientoUI,
    onVerDetalle: () -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment     = Alignment.Top
        ) {
            Text(
                text     = est.nombre,
                style    = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.weight(1f)
            )
            Spacer(Modifier.width(8.dp))
            TipoBadge(est.tipo)
        }

        RatingRow(est.rating)

        Row(
            verticalAlignment     = Alignment.Top,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint     = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                modifier = Modifier.size(16.dp).padding(top = 2.dp)
            )
            Text(
                text  = est.direccion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        est.distanciaKm?.let { km ->
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text       = "%.1f km desde tu ubicación".format(km),
                    style      = MaterialTheme.typography.labelMedium,
                    color      = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontWeight = FontWeight.SemiBold,
                    modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

        Row(
            modifier              = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                Text("Cerrar")
            }
            Button(onClick = onVerDetalle, modifier = Modifier.weight(1f)) {
                Text("Ver detalle")
            }
        }
    }
}

// ── Sub-componentes ───────────────────────────────────────────────────────────

@Composable
private fun FiltroItem(
    color: Color,
    label: String,
    activo: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape    = RoundedCornerShape(8.dp),
        color    = if (activo) color.copy(alpha = 0.14f) else Color.Transparent,
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 4.dp)
    ) {
        Row(
            modifier              = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment     = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(if (activo) 11.dp else 9.dp)
                    .background(color, RoundedCornerShape(50))
            )
            Text(
                text  = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (activo) FontWeight.Bold else FontWeight.Normal,
                    color      = if (activo) color else MaterialTheme.colorScheme.onSurface
                )
            )
            if (activo) {
                Icon(
                    Icons.Default.Close,
                    contentDescription = "Quitar filtro",
                    tint     = color,
                    modifier = Modifier.size(11.dp)
                )
            }
        }
    }
}

@Composable
private fun TipoBadge(tipo: TipoEstablecimiento) {
    val (label, color) = when (tipo) {
        is TipoEstablecimiento.Taller   -> "Taller"   to ColorTaller
        is TipoEstablecimiento.Lavadero -> "Lavadero" to ColorLavadero
        is TipoEstablecimiento.Otro     -> tipo.nombre.ifBlank { "Otro" } to ColorGris
    }
    Surface(shape = RoundedCornerShape(4.dp), color = color.copy(alpha = 0.12f)) {
        Text(
            text       = label,
            color      = color,
            fontWeight = FontWeight.Bold,
            fontSize   = 11.sp,
            modifier   = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun RatingRow(rating: Double) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        val filled = rating.roundToInt().coerceIn(0, 5)
        repeat(5) { i ->
            Icon(
                Icons.Default.Star,
                contentDescription = null,
                tint     = if (i < filled) ColorEstrella else ColorGris,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(4.dp))
        Text(
            text       = "%.1f".format(rating),
            style      = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color      = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
        )
    }
}

// ── Bitmap de marcador personalizado ─────────────────────────────────────────

private fun pinBitmap(context: Context, hue: Float, selected: Boolean): BitmapDescriptor {
    val dp  = context.resources.displayMetrics.density
    val w   = ((if (selected) 42 else 28) * dp).toInt()
    val h   = ((if (selected) 62 else 42) * dp).toInt()
    val bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bmp)

    val hsv      = floatArrayOf(hue, 0.88f, 0.82f)
    val mainArgb = android.graphics.Color.HSVToColor(hsv)

    val cx      = w / 2f
    val circleR = w * 0.44f
    val circleY = circleR + dp * 2f

    val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = mainArgb
        style = Paint.Style.FILL
    }

    // Cola triangular
    val path = Path().apply {
        moveTo(cx - circleR * 0.4f, circleY + circleR * 0.6f)
        lineTo(cx, h.toFloat() - dp * 2f)
        lineTo(cx + circleR * 0.4f, circleY + circleR * 0.6f)
        close()
    }
    canvas.drawPath(path, paint)
    canvas.drawCircle(cx, circleY, circleR, paint)

    if (selected) {
        paint.color       = android.graphics.Color.WHITE
        paint.style       = Paint.Style.STROKE
        paint.strokeWidth = dp * 2.5f
        canvas.drawCircle(cx, circleY, circleR - dp * 1.5f, paint)
        paint.style = Paint.Style.FILL
    }

    paint.color = android.graphics.Color.WHITE
    canvas.drawCircle(cx, circleY, circleR * 0.36f, paint)

    return BitmapDescriptorFactory.fromBitmap(bmp)
}
