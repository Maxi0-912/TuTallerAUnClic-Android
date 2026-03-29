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
                    model = establecimiento.imagenUrl,
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

                Text(
                    text = "Desde ${establecimiento.precioDesde}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )

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