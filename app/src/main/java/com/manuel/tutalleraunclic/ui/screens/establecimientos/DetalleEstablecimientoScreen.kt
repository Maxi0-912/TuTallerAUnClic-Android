package com.manuel.tutalleraunclic.ui.screens.establecimientos

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.manuel.tutalleraunclic.core.navigation.Routes
import com.manuel.tutalleraunclic.data.model.entity.Calificacion
import com.manuel.tutalleraunclic.data.model.entity.Establecimiento
import com.manuel.tutalleraunclic.data.model.entity.Servicio
import com.manuel.tutalleraunclic.utils.fixImageUrl
import com.manuel.tutalleraunclic.viewmodel.DetalleViewModel

// ─── Paleta local ─────────────────────────────────────────────────────────────
private val Azul         = Color(0xFF2563EB)
private val AzulClaro    = Color(0xFF3B82F6)
private val Morado       = Color(0xFF7C3AED)
private val Estrella     = Color(0xFFFACC15)
private val EstrellaBaja = Color(0xFFD1D5DB)

// ═══════════════════════════════════════════════════════════════════════════════
// PANTALLA PRINCIPAL
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
fun DetalleEstablecimientoScreen(
    establecimientoId: Int,
    navController: NavController,
    viewModel: DetalleViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(establecimientoId) {
        viewModel.cargar(establecimientoId)
    }

    val est = state.establecimiento

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1 ── HEADER ──────────────────────────────────────────────────────
            item {
                HeaderBanner(
                    establecimiento = est,
                    onBack = { navController.popBackStack() }
                )
            }

            // 2 ── INFO PRINCIPAL ─────────────────────────────────────────────
            item {
                if (est != null) {
                    InfoPrincipal(
                        establecimiento  = est,
                        totalResenas     = state.resenas.size
                    )
                }
            }

            // 3 ── UBICACIÓN ──────────────────────────────────────────────────
            item {
                if (est != null) {
                    val lat = est.latitud.toDoubleOrNull()
                    val lng = est.longitud.toDoubleOrNull()
                    if (lat != null && lng != null && lat != 0.0 && lng != 0.0) {
                        UbicacionSection(
                            direccion = est.direccion,
                            lat       = lat,
                            lng       = lng,
                            nombre    = est.nombre
                        )
                    }
                }
            }

            // 4 ── SERVICIOS ──────────────────────────────────────────────────
            item {
                if (state.servicios.isNotEmpty()) {
                    SeccionTitulo(titulo = "Servicios", icono = Icons.Default.Build)
                }
            }
            items(state.servicios) { servicio ->
                ServicioCard(
                    servicio  = servicio,
                    onAgendar = {
                        navController.navigate(
                            Routes.cita(establecimientoId, servicio.id)
                        )
                    }
                )
            }
            item {
                if (state.servicios.isEmpty() && est != null) {
                    SeccionTitulo(titulo = "Servicios", icono = Icons.Default.Build)
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)
                    ) {
                        Text(
                            "Sin servicios registrados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }

            // 5 ── PROMOCIONES ────────────────────────────────────────────────
            item {
                SeccionTitulo(titulo = "Ofertas especiales", icono = Icons.Default.Star)
                PromocionesSection()
            }

            // 6 ── RESEÑAS ────────────────────────────────────────────────────
            item {
                if (state.resenas.isNotEmpty()) {
                    SeccionTitulo(titulo = "Reseñas de clientes", icono = Icons.Default.Person)
                }
            }
            items(state.resenas.take(10)) { resena ->
                ResenaCard(resena = resena)
            }
            item {
                if (state.resenas.isEmpty() && est != null) {
                    SeccionTitulo(titulo = "Reseñas de clientes", icono = Icons.Default.Person)
                    Box(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 8.dp)) {
                        Text(
                            "Aún no hay reseñas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        // Indicador de carga sobre el contenido
        if (state.isLoading && est == null) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color    = Azul
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// 1. HEADER BANNER
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun HeaderBanner(
    establecimiento: Establecimiento?,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        // Imagen del establecimiento (o placeholder gris)
        AsyncImage(
            model = fixImageUrl(establecimiento?.foto_url)
                ?: "https://images.unsplash.com/photo-1558618666-fcd25c85cd64?w=800",
            contentDescription = establecimiento?.nombre ?: "Establecimiento",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Degradado: oscuro arriba (botón) → transparente en medio → oscuro abajo (nombre)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.00f to Color.Black.copy(alpha = 0.55f),
                        0.35f to Color.Transparent,
                        1.00f to Color.Black.copy(alpha = 0.80f)
                    )
                )
        )

        // Botón volver — esquina superior izquierda
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .statusBarsPadding()
                .padding(12.dp)
                .size(40.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.35f))
        ) {
            Icon(
                imageVector     = Icons.Default.ArrowBack,
                contentDescription = "Volver",
                tint            = Color.White
            )
        }

        // Nombre del establecimiento — esquina inferior izquierda
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // Badge de tipo
            establecimiento?.tipo_nombre?.let { tipo ->
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Azul.copy(alpha = 0.85f)
                ) {
                    Text(
                        text     = tipo.replaceFirstChar { it.uppercase() },
                        color    = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
            Text(
                text       = establecimiento?.nombre ?: "",
                style      = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                ),
                maxLines   = 2,
                overflow   = TextOverflow.Ellipsis
            )
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// 2. INFO PRINCIPAL
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun InfoPrincipal(
    establecimiento: Establecimiento,
    totalResenas: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Nombre + tipo (texto plano, el badge está en el header)
        Text(
            text  = establecimiento.nombre,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
        )

        // Rating row
        val rating = establecimiento.promedio_calificacion ?: 0.0
        EstrellaRating(rating = rating, total = totalResenas)

        // Chips secundarios
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (establecimiento.telefono.isNotBlank()) {
                InfoChip(
                    icono   = Icons.Default.Phone,
                    texto   = establecimiento.telefono,
                    fondo   = MaterialTheme.colorScheme.secondaryContainer,
                    textoColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            val horario = "${establecimiento.hora_apertura} – ${establecimiento.hora_cierre}"
            if (establecimiento.hora_apertura.isNotBlank()) {
                InfoChip(
                    icono      = Icons.Default.Build,
                    texto      = horario,
                    fondo      = MaterialTheme.colorScheme.secondaryContainer,
                    textoColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        // Descripción
        if (establecimiento.descripcion.isNotBlank()) {
            Text(
                text  = establecimiento.descripcion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }
    }

    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
}

@Composable
private fun InfoChip(
    icono: ImageVector,
    texto: String,
    fondo: Color,
    textoColor: Color
) {
    Surface(shape = RoundedCornerShape(20.dp), color = fondo) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(icono, contentDescription = null, modifier = Modifier.size(14.dp), tint = textoColor)
            Text(texto, style = MaterialTheme.typography.labelSmall, color = textoColor, maxLines = 1)
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// 3. UBICACIÓN + MINI MAPA
// ═══════════════════════════════════════════════════════════════════════════════

@Composable
private fun UbicacionSection(
    direccion: String,
    lat: Double,
    lng: Double,
    nombre: String
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        SeccionTitulo(titulo = "Ubicación", icono = Icons.Default.LocationOn)

        // Dirección textual
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                Icons.Default.LocationOn,
                contentDescription = null,
                tint     = Azul,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text  = direccion,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
            )
        }

        Spacer(Modifier.height(10.dp))

        // Mini mapa no interactivo
        val latLng      = LatLng(lat, lng)
        val cameraState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(latLng, 15f)
        }
        Card(
            modifier  = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .height(200.dp),
            shape     = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(2.dp)
        ) {
            GoogleMap(
                modifier             = Modifier.fillMaxSize(),
                cameraPositionState  = cameraState,
                uiSettings           = MapUiSettings(
                    scrollGesturesEnabled   = false,
                    zoomGesturesEnabled     = false,
                    zoomControlsEnabled     = false,
                    rotationGesturesEnabled = false,
                    tiltGesturesEnabled     = false,
                    myLocationButtonEnabled = false,
                    mapToolbarEnabled       = false
                )
            ) {
                Marker(
                    state = MarkerState(position = latLng),
                    title = nombre
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
            color    = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// 5. PROMOCIONES (datos de ejemplo visuales)
// ═══════════════════════════════════════════════════════════════════════════════

private data class Promo(val titulo: String, val subtitulo: String, val descuento: String)

private val PROMOS_EJEMPLO = listOf(
    Promo("Cambio de aceite",   "Incluye revisión general",     "20% OFF"),
    Promo("Lavado completo",    "Interior + exterior + aroma",  "15% OFF"),
    Promo("Revisión de frenos", "Diagnóstico sin costo",        "GRATIS"),
)

@Composable
private fun PromocionesSection() {
    LazyRow(
        contentPadding      = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(PROMOS_EJEMPLO) { promo ->
            PromoCard(promo)
        }
    }
    Spacer(Modifier.height(8.dp))
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
        color    = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
    )
}

@Composable
private fun PromoCard(promo: Promo) {
    Box(
        modifier = Modifier
            .width(200.dp)
            .height(110.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(listOf(Azul, Morado))
            )
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
            Text(
                text       = promo.titulo,
                style      = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color      = Color.White
                ),
                maxLines   = 1,
                overflow   = TextOverflow.Ellipsis
            )
            Text(
                text  = promo.subtitulo,
                style = MaterialTheme.typography.labelSmall.copy(color = Color.White.copy(alpha = 0.8f)),
                maxLines = 2
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.White.copy(alpha = 0.2f)
            ) {
                Text(
                    text     = promo.descuento,
                    style    = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color      = Color.White
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ═══════════════════════════════════════════════════════════════════════════════
// COMPONENTES REUTILIZABLES
// ═══════════════════════════════════════════════════════════════════════════════

/** Encabezado de sección con ícono de acento azul. */
@Composable
fun SeccionTitulo(titulo: String, icono: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Azul.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icono, contentDescription = null, tint = Azul, modifier = Modifier.size(18.dp))
        }
        Text(
            text  = titulo,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}

/** Fila de estrellas con número de rating y contador de reseñas. */
@Composable
fun EstrellaRating(rating: Double, total: Int) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 5 estrellas coloreadas según el rating
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            repeat(5) { index ->
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = if (index < rating) Estrella else EstrellaBaja,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
        Text(
            text  = "%.1f".format(rating),
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
        )
        Text(
            text  = "($total reseñas)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        )
    }
}

/** Card horizontal de servicio con ícono y botón "Agendar". */
@Composable
fun ServicioCard(servicio: Servicio, onAgendar: () -> Unit) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Ícono de llave/herramienta con fondo azul
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Azul.copy(alpha = 0.10f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    tint        = Azul,
                    modifier    = Modifier.size(26.dp)
                )
            }

            // Nombre
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text   = servicio.nombre,
                    style  = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text  = "Servicio disponible",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                )
            }

            // Botón agendar
            Button(
                onClick = onAgendar,
                shape   = RoundedCornerShape(10.dp),
                colors  = ButtonDefaults.buttonColors(containerColor = Azul),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text("Agendar", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/** Card de reseña con avatar circular, estrellas y comentario. */
@Composable
fun ResenaCard(resena: Calificacion) {
    Card(
        modifier  = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 5.dp),
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Avatar circular con inicial
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(AzulClaro, Morado))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text       = "U",
                        style      = MaterialTheme.typography.titleMedium.copy(
                            color      = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text  = "Usuario verificado",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text  = resena.fecha.take(10),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    )
                }

                // Estrellas compactas
                Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                    repeat(5) { index ->
                        Icon(
                            Icons.Default.Star,
                            contentDescription = null,
                            tint     = if (index < resena.puntuacion) Estrella else EstrellaBaja,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            if (resena.comentario.isNotBlank()) {
                Text(
                    text  = resena.comentario,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.80f)
                )
            }
        }
    }
}
