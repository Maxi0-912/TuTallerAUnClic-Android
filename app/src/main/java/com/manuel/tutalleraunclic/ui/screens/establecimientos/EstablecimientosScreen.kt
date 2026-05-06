package com.manuel.tutalleraunclic.ui.screens.establecimientos

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import com.manuel.tutalleraunclic.utils.fixImageUrl
import com.manuel.tutalleraunclic.core.navigation.Routes
import com.manuel.tutalleraunclic.data.model.EstablecimientoUI
import com.manuel.tutalleraunclic.data.model.TipoEstablecimiento
import com.manuel.tutalleraunclic.viewmodel.EstablecimientoViewModel
import kotlinx.coroutines.delay

// ─── Banners de ejemplo ───────────────────────────────────────────────────────

private data class BannerItem(
    val titulo: String,
    val descripcion: String,
    val colores: List<Color>
)

private val BANNERS = listOf(
    BannerItem(
        titulo = "Tu vehículo merece lo mejor",
        descripcion = "Encuentra talleres y lavaderos cerca de ti, rápido y fácil",
        colores = listOf(Color(0xFF2563EB), Color(0xFF7C3AED))
    ),
    BannerItem(
        titulo = "Agenda en segundos",
        descripcion = "Sin esperas, sin llamadas. Solo toca y reserva tu cita",
        colores = listOf(Color(0xFF059669), Color(0xFF0891B2))
    )
)

// ─── Pantalla principal ───────────────────────────────────────────────────────

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun EstablecimientosScreen(navController: NavController) {

    val viewModel: EstablecimientoViewModel = hiltViewModel()
    val context = LocalContext.current

    // Solicitar ubicación
    val locationLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val granted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                      permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) solicitarUbicacion(context, viewModel)
    }

    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (granted) solicitarUbicacion(context, viewModel)
        else locationLauncher.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    // Filtros por tipo
    val talleres by remember { derivedStateOf {
        viewModel.lista.filter {
            it.tipo is TipoEstablecimiento.Taller ||
            it.tipoNombre?.lowercase()?.contains("taller") == true
        }
    }}
    val lavaderos by remember { derivedStateOf {
        viewModel.lista.filter {
            it.tipo is TipoEstablecimiento.Lavadero ||
            it.tipoNombre?.lowercase()?.let { n -> n.contains("lavadero") || n.contains("lava") } == true
        }
    }}

    // Banner pager
    val pagerState = rememberPagerState(pageCount = { BANNERS.size })
    LaunchedEffect(pagerState) {
        while (true) {
            delay(4500)
            pagerState.animateScrollToPage((pagerState.currentPage + 1) % BANNERS.size)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "TuTallerAUnClic",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {

            // ── BANNER CARRUSEL ──────────────────────────────────────────────
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    HorizontalPager(
                        state = pagerState,
                        modifier = Modifier.fillMaxWidth()
                    ) { page ->
                        BannerCard(banner = BANNERS[page])
                    }
                    Spacer(Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(BANNERS.size) { idx ->
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .size(if (idx == pagerState.currentPage) 10.dp else 6.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        if (idx == pagerState.currentPage)
                                            MaterialTheme.colorScheme.primary
                                        else
                                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.25f)
                                    )
                            )
                        }
                    }
                }
            }

            // ── LAVADEROS ────────────────────────────────────────────────────
            item {
                SeccionTitulo(titulo = "🚿 Lavaderos cerca de ti")
            }
            item {
                when {
                    viewModel.isLoading -> PlaceholderLoading()
                    lavaderos.isEmpty() -> PlaceholderVacio("Sin lavaderos disponibles")
                    else -> LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(lavaderos, key = { it.id }) { est ->
                            MiniCard(
                                establecimiento = est,
                                onClick = { navController.navigate(Routes.detalle(est.id.toString())) }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // ── TALLERES ─────────────────────────────────────────────────────
            item {
                SeccionTitulo(titulo = "🔧 Talleres cerca de ti")
            }
            item {
                when {
                    viewModel.isLoading -> PlaceholderLoading()
                    talleres.isEmpty() -> PlaceholderVacio("Sin talleres disponibles")
                    else -> LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(talleres, key = { it.id }) { est ->
                            MiniCard(
                                establecimiento = est,
                                onClick = { navController.navigate(Routes.detalle(est.id.toString())) }
                            )
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // Error (solo si la lista está vacía y no está cargando)
            if (!viewModel.isLoading && viewModel.lista.isEmpty() && viewModel.error != null) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No se pudieron cargar los establecimientos")
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { viewModel.cargarEstablecimientos() }) {
                            Text("Reintentar")
                        }
                    }
                }
            }
        }
    }
}

// ─── Composables privados ─────────────────────────────────────────────────────

@Composable
private fun BannerCard(banner: BannerItem) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(155.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Brush.horizontalGradient(banner.colores))
            .padding(20.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = banner.titulo,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = banner.descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.88f)
            )
        }
    }
}

@Composable
private fun SeccionTitulo(titulo: String) {
    Text(
        text = titulo,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun MiniCard(establecimiento: EstablecimientoUI, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(175.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column {
            AsyncImage(
                model = fixImageUrl(establecimiento.imagenUrl),
                contentDescription = establecimiento.nombre,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(105.dp)
            )
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = establecimiento.nombre,
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(3.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(13.dp)
                    )
                    Text(
                        text = "%.1f".format(establecimiento.rating),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
                establecimiento.distanciaKm?.let { km ->
                    Text(
                        text = if (km < 1.0) "${"%.0f".format(km * 1000)} m" else "${"%.1f".format(km)} km",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                }
            }
        }
    }
}

@Composable
private fun PlaceholderLoading() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(155.dp),
        contentAlignment = Alignment.Center
    ) { CircularProgressIndicator() }
}

@Composable
private fun PlaceholderVacio(mensaje: String) {
    Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
        Text(
            text = mensaje,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

// ─── Ubicación ────────────────────────────────────────────────────────────────

private fun solicitarUbicacion(context: android.content.Context, viewModel: EstablecimientoViewModel) {
    try {
        LocationServices.getFusedLocationProviderClient(context)
            .lastLocation
            .addOnSuccessListener { location ->
                if (location != null) viewModel.actualizarUbicacion(location.latitude, location.longitude)
            }
    } catch (_: SecurityException) {}
}
