package com.manuel.tutalleraunclic.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.navigation.NavController
import com.manuel.tutalleraunclic.core.navigation.Routes
import com.manuel.tutalleraunclic.data.model.entity.Anuncio

/** true si la tarjeta debe reaccionar al toque (accion "agendar" o "enlace"). */
fun esAnuncioClicable(anuncio: Anuncio): Boolean = anuncio.accion == "agendar" || anuncio.accion == "enlace"

/**
 * Ejecuta la acción calculada por el backend para este anuncio.
 * Se usa tanto en el carrusel de home como en la sección de ofertas del detalle
 * de establecimiento, para que ambos se comporten igual.
 */
fun manejarClickAnuncio(context: Context, navController: NavController, anuncio: Anuncio) {
    when (anuncio.accion) {
        "agendar" -> {
            val servicioId = anuncio.servicio?.id ?: return
            navController.navigate(
                Routes.cita(
                    establecimientoId = anuncio.establecimiento,
                    servicioId = servicioId,
                    anuncioId = anuncio.id,
                    anuncioTitulo = anuncio.titulo,
                    anuncioDescuento = anuncio.descuento
                )
            )
        }
        "enlace" -> {
            val url = anuncio.url_boton
            if (!url.isNullOrBlank()) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            }
        }
    }
}
