package com.manuel.tutalleraunclic.ui.screens.establecimientos

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.location.LocationServices
import com.manuel.tutalleraunclic.R
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
    val colores: List<Color>,
    val icono: String,
    val etiqueta: String,
    val imageUrl: String
)

private val BANNERS = listOf(
    BannerItem(
        titulo = "Tu vehículo merece lo mejor",
        descripcion = "Encuentra talleres y lavaderos cerca de ti, rápido y fácil",
        colores = listOf(Color(0xFF1E293B), Color(0xFF334155)),
        icono = "🔧",
        etiqueta = "DESTACADO",
        imageUrl = "https://images.unsplash.com/photo-1486262715619-67b85e0b08d3?w=800"
    ),
    BannerItem(
        titulo = "Agenda en segundos",
        descripcion = "Sin esperas, sin llamadas. Solo toca y reserva tu cita",
        colores = listOf(Color(0xFF0F172A), Color(0xFF1E3A5F)),
        icono = "💧",
        etiqueta = "OFERTA",
        imageUrl = "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800"
    ),
    BannerItem(
        titulo = "¡Agenda ahora!",
        descripcion = "Disponibilidad limitada, reserva tu cita hoy mismo",
        colores = listOf(Color(0xFF1A1A2E), Color(0xFF16213E)),
        icono = "🔧",
        etiqueta = "OFERTA",
        imageUrl = "https://images.unsplash.com/photo-1607860108855-64acf2078ed9?w=800"
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
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_solo),
                            contentDescription = "Tu Taller a un Clic",
                            modifier = Modifier.size(40.dp).clip(CircleShape)
                        )
                    }
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
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        repeat(BANNERS.size) { idx ->
                            val selected = idx == pagerState.currentPage
                            val dotSize by animateDpAsState(
                                targetValue = if (selected) 12.dp else 7.dp,
                                label = "dot_size"
                            )
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 3.dp)
                                    .size(dotSize)
                                    .clip(RoundedCornerShape(50))
                                    .background(
                                        if (selected)
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
                SeccionTitulo(titulo = "Lavaderos cerca de ti", icono = Icons.Default.WaterDrop)
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

            item { Spacer(Modifier.height(8.dp)) }

            // ── TALLERES ─────────────────────────────────────────────────────
            item {
                SeccionTitulo(titulo = "Talleres cerca de ti", icono = Icons.Default.Build)
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
            .height(200.dp)
            .clip(RoundedCornerShape(20.dp))
    ) {
        AsyncImage(
            model = banner.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF000000).copy(alpha = 0.35f),
                            Color(0xFF000000).copy(alpha = 0.75f)
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = banner.etiqueta,
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = banner.titulo,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold),
                    color = Color.White
                )
                Text(
                    text = banner.descripcion,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.88f)
                )
            }
            Box(
                modifier = Modifier
                    .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Ver más →",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun SeccionTitulo(
    titulo: String,
    icono: ImageVector? = null,
    onVerTodos: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 4.dp, top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icono != null) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icono,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onVerTodos) {
            Text(
                text = "Ver todos →",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun MiniCard(establecimiento: EstablecimientoUI, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(200.dp)
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
                    .height(120.dp)
            )
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = establecimiento.nombre,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
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
                if (establecimiento.direccion.isNotBlank()) {
                    Text(
                        text = establecimiento.direccion,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
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
