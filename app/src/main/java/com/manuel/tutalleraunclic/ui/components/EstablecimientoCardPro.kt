package com.manuel.tutalleraunclic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import coil.compose.AsyncImage
import com.manuel.tutalleraunclic.data.model.EstablecimientoUI
import com.manuel.tutalleraunclic.utils.fixImageUrl
import com.manuel.tutalleraunclic.data.model.TipoEstablecimiento

@Composable
fun EstablecimientoCardPro(
    establecimiento: EstablecimientoUI,
    onClick: () -> Unit,
    onAgendarClick: () -> Unit
) {

    Card(
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {

        Column {

            // 🔥 Imagen con overlay
            Box {

                AsyncImage(
                    model = fixImageUrl(establecimiento.imagenUrl),
                    contentDescription = establecimiento.nombre,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.6f)
                                )
                            )
                        )
                )

                Text(
                    text = establecimiento.nombre,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(12.dp)
                )
            }

            Column(modifier = Modifier.padding(12.dp)) {

                // TIPO BADGE
                val (tipoLabel, tipoColor) = when {
                    establecimiento.tipo is TipoEstablecimiento.Taller -> "🔧 Taller" to Color(0xFF2563EB)
                    establecimiento.tipo is TipoEstablecimiento.Lavadero -> "🚿 Lavadero" to Color(0xFF16A34A)
                    establecimiento.tipoNombre?.lowercase()?.contains("taller") == true -> "🔧 Taller" to Color(0xFF2563EB)
                    establecimiento.tipoNombre?.lowercase()?.let { it.contains("lavadero") || it.contains("lava") } == true -> "🚿 Lavadero" to Color(0xFF16A34A)
                    else -> null to null
                }
                if (tipoLabel != null && tipoColor != null) {
                    Surface(
                        color = tipoColor.copy(alpha = 0.12f),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text(
                            text = tipoLabel,
                            style = MaterialTheme.typography.labelSmall,
                            color = tipoColor,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Text(
                    text = establecimiento.direccion,
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {

                    repeat(5) { index ->
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = if (index < establecimiento.rating.toInt())
                                Color(0xFFFFC107)
                            else
                                Color.Gray
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text("(${establecimiento.totalReviews})")
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Desde ${establecimiento.precioDesde}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    establecimiento.distanciaKm?.let { km ->
                        Text(
                            text = if (km < 1.0) "${"%.0f".format(km * 1000)} m" else "${"%.1f".format(km)} km",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onAgendarClick,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Agendar cita")
                }
            }
        }
    }
}