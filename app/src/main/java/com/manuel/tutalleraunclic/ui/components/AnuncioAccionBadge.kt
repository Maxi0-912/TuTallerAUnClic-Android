package com.manuel.tutalleraunclic.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Affordance sutil que indica que una tarjeta de anuncio es tocable, y hacia
 * qué (agendar una cita o abrir un enlace externo). Se usa en el carrusel de
 * home y en la sección de ofertas del detalle de establecimiento.
 */
@Composable
fun AnuncioAccionBadge(accion: String?, modifier: Modifier = Modifier) {
    val icono = when (accion) {
        "agendar" -> Icons.Default.CalendarToday
        "enlace"  -> Icons.Default.OpenInNew
        else      -> return
    }
    Icon(
        imageVector = icono,
        contentDescription = null,
        tint = Color.White,
        modifier = modifier
            .size(26.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.4f))
            .padding(5.dp)
    )
}
