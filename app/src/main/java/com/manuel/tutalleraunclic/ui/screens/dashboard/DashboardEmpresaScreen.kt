package com.manuel.tutalleraunclic.ui.screens.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import coil.compose.AsyncImage
import com.manuel.tutalleraunclic.R
import com.manuel.tutalleraunclic.data.model.response.EstablecimientoDashboard
import com.manuel.tutalleraunclic.utils.fixImageUrl
import com.manuel.tutalleraunclic.viewmodel.DashboardEmpresaViewModel

private val AzulStat = Color(0xFF2563EB)
private val VerdeStat = Color(0xFF16A34A)
private val AmbarStat = Color(0xFFCA8A04)

private val COLOR_ESTADO = mapOf(
    "pendiente" to Color(0xFFF59E0B),
    "confirmada" to Color(0xFF3B82F6),
    "finalizada" to Color(0xFF10B981),
    "cancelada" to Color(0xFF6B7280),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardEmpresaScreen(
    onVerCitas: () -> Unit,
    onRegistrarEstablecimiento: () -> Unit,
    viewModel: DashboardEmpresaViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.cargarDashboard()
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
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
                        Text("Dashboard", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }
    ) { padding ->

        val data = state.data

        when {
            state.isLoading && data == null -> {
                Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            data == null -> {
                Box(Modifier.padding(padding).fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            state.error ?: "Error al cargar el dashboard",
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { viewModel.cargarDashboard() }) { Text("Reintentar") }
                    }
                }
            }
            else -> {
                val rg = data.resumen_general
                val pe = data.por_establecimiento

                Column(
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Column {
                        Text("Resumen de tu actividad este mes", style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                    }

                    // ── Tarjetas de resumen ─────────────────────────────────────
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            StatCard("ESTABLECIMIENTOS", "${rg.total_establecimientos}", AzulStat, Modifier.weight(1f))
                            StatCard("CITAS ESTE MES", "${rg.total_citas_mes}", VerdeStat, Modifier.weight(1f))
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                            StatCard("PENDIENTES HOY", "${rg.pendientes_hoy}", AmbarStat, Modifier.weight(1f))
                            StatCard(
                                "CALIFICACIÓN PROM",
                                rg.calificacion_promedio?.let { "%.1f ★".format(it) } ?: "—",
                                AmbarStat,
                                Modifier.weight(1f)
                            )
                        }
                    }

                    // ── Comparativa simple (sin librería de gráficas) ────────────
                    if (pe.size > 1) {
                        ComparativaCitas(pe)
                    }

                    // ── Por establecimiento ───────────────────────────────────────
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text("Por establecimiento", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)

                        if (pe.isEmpty()) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text("Aún no tienes establecimientos", fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    "Registra tu taller o lavadero para empezar",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Spacer(Modifier.height(16.dp))
                                Button(onClick = onRegistrarEstablecimiento) {
                                    Text("Registrar establecimiento")
                                }
                            }
                        } else {
                            pe.forEach { item ->
                                EstablecimientoDashboardCard(item = item, onVerCitas = onVerCitas)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(14.dp)) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
            Spacer(Modifier.height(6.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

@Composable
private fun StatMini(value: String, label: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Black, color = color)
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
    }
}

@Composable
private fun ComparativaCitas(establecimientos: List<EstablecimientoDashboard>) {
    val max = (establecimientos.maxOfOrNull { it.total_citas_mes } ?: 0).coerceAtLeast(1)
    Card(shape = RoundedCornerShape(14.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Citas por establecimiento este mes", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            establecimientos.forEach { est ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        est.nombre,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.width(80.dp)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(16.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(est.total_citas_mes.toFloat() / max)
                                .clip(RoundedCornerShape(4.dp))
                                .background(AzulStat)
                        )
                    }
                    Text("${est.total_citas_mes}", style = MaterialTheme.typography.labelSmall, modifier = Modifier.width(24.dp))
                }
            }
        }
    }
}

@Composable
private fun EstablecimientoDashboardCard(
    item: EstablecimientoDashboard,
    onVerCitas: () -> Unit
) {
    Card(shape = RoundedCornerShape(16.dp)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    if (!item.foto_url.isNullOrBlank()) {
                        AsyncImage(
                            model = fixImageUrl(item.foto_url),
                            contentDescription = item.nombre,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Text(if (item.tipo.equals("Taller", ignoreCase = true)) "🔧" else "💧", style = MaterialTheme.typography.titleLarge)
                    }
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.nombre, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                    Text(item.tipo, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                }
                item.calificacion?.let {
                    Text("%.1f ★".format(it), fontWeight = FontWeight.Bold, color = Color(0xFFEAB308))
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                StatMini("${item.total_citas_mes}", "Citas este mes", AzulStat, Modifier.weight(1f))
                StatMini("${item.pendientes_hoy}", "Pendientes hoy", AmbarStat, Modifier.weight(1f))
            }

            if (item.citas_por_estado.isNotEmpty()) {
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    item.citas_por_estado.forEach { s ->
                        val color = COLOR_ESTADO[s.estado.lowercase()] ?: Color(0xFF6B7280)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(color.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                "${s.estado}: ${s.total}",
                                style = MaterialTheme.typography.labelSmall,
                                color = color,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            OutlinedButton(onClick = onVerCitas, modifier = Modifier.fillMaxWidth()) {
                Text("Ver citas")
            }
        }
    }
}
