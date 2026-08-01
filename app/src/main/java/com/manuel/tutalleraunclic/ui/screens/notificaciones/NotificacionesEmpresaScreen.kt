package com.manuel.tutalleraunclic.ui.screens.notificaciones

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.manuel.tutalleraunclic.data.model.entity.Notificacion
import com.manuel.tutalleraunclic.viewmodel.NotificacionesViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private val FORMATOS_FECHA = listOf(
    "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'" to true,
    "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'" to true,
    "yyyy-MM-dd'T'HH:mm:ss'Z'" to true,
    "yyyy-MM-dd'T'HH:mm:ss" to false,
    "yyyy-MM-dd HH:mm:ss" to false,
    "yyyy-MM-dd" to false,
)

private fun parseFechaMillis(fecha: String): Long? {
    for ((patron, esUtc) in FORMATOS_FECHA) {
        try {
            val sdf = SimpleDateFormat(patron, Locale.US)
            if (esUtc) sdf.timeZone = TimeZone.getTimeZone("UTC")
            return sdf.parse(fecha)?.time
        } catch (_: Exception) { /* probar siguiente formato */ }
    }
    return null
}

private fun tiempoRelativo(fecha: String): String {
    val momento = parseFechaMillis(fecha) ?: return fecha
    val diff = System.currentTimeMillis() - momento
    val min = diff / 60_000
    val hrs = diff / 3_600_000
    val dias = diff / 86_400_000
    return when {
        min < 1 -> "Ahora mismo"
        min < 60 -> "Hace $min min"
        hrs < 24 -> "Hace $hrs h"
        else -> "Hace $dias día${if (dias != 1L) "s" else ""}"
    }
}

private enum class FiltroNotif(val label: String) {
    TODAS("Todas"), SIN_LEER("Sin leer"), LEIDAS("Leídas")
}

@Composable
fun NotificacionesEmpresaScreen(
    viewModel: NotificacionesViewModel
) {
    val notificaciones by viewModel.notificaciones.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val unreadCount by viewModel.unreadCount.collectAsState()

    var filtro by remember { mutableStateOf(FiltroNotif.TODAS) }

    val filtradas = remember(notificaciones, filtro) {
        when (filtro) {
            FiltroNotif.TODAS -> notificaciones
            FiltroNotif.SIN_LEER -> notificaciones.filter { !it.leida }
            FiltroNotif.LEIDAS -> notificaciones.filter { it.leida }
        }
    }

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {

            // ── Encabezado ────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column {
                    Text("Notificaciones", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    Text(
                        if (unreadCount > 0) "$unreadCount sin leer" else "Todo al día",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
                if (unreadCount > 0) {
                    OutlinedButton(onClick = { viewModel.marcarTodasLeidas() }) {
                        Text("Marcar todas como leídas")
                    }
                }
            }

            // ── Chips de filtro ───────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FiltroNotif.entries.forEach { f ->
                    val cantidad = when (f) {
                        FiltroNotif.TODAS -> notificaciones.size
                        FiltroNotif.SIN_LEER -> unreadCount
                        FiltroNotif.LEIDAS -> notificaciones.size - unreadCount
                    }
                    ChipFiltro(
                        label = f.label,
                        cantidad = cantidad,
                        seleccionado = filtro == f,
                        onClick = { filtro = f }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // ── Lista ─────────────────────────────────────────────────────────
            when {
                isLoading && notificaciones.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                filtradas.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(
                                "Sin notificaciones",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(
                            items = filtradas.sortedByDescending { it.id },
                            key = { it.id }
                        ) { notif ->
                            NotificacionEmpresaCard(
                                notificacion = notif,
                                onClick = { if (!notif.leida) viewModel.marcarLeida(notif.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ChipFiltro(
    label: String,
    cantidad: Int,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val bg = if (seleccionado) MaterialTheme.colorScheme.primary else Color.Transparent
    val fg = if (seleccionado) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
    val borderColor = if (seleccionado) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(bg)
            .border(1.dp, borderColor, RoundedCornerShape(50))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        Text(
            "$label $cantidad",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium),
            color = fg
        )
    }
}

@Composable
private fun NotificacionEmpresaCard(
    notificacion: Notificacion,
    onClick: () -> Unit
) {
    val bg = if (!notificacion.leida)
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
    else
        MaterialTheme.colorScheme.surface

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (!notificacion.leida) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.surfaceVariant
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (!notificacion.leida) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                contentDescription = null,
                tint = if (!notificacion.leida) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                modifier = Modifier.size(18.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    notificacion.titulo,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = if (!notificacion.leida) FontWeight.Bold else FontWeight.Normal
                    ),
                    color = if (!notificacion.leida) MaterialTheme.colorScheme.onSurface
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.weight(1f, fill = false)
                )
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        tiempoRelativo(notificacion.fecha),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                    )
                    if (!notificacion.leida) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                notificacion.mensaje,
                style = MaterialTheme.typography.bodySmall,
                color = if (!notificacion.leida) MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }
}
