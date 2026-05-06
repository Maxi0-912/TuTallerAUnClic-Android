package com.manuel.tutalleraunclic.data.model

import com.manuel.tutalleraunclic.data.model.entity.Establecimiento
import com.manuel.tutalleraunclic.utils.fixImageUrl
import kotlin.math.*

fun Establecimiento.toUI(userLat: Double? = null, userLng: Double? = null): EstablecimientoUI {
    val lat = latitud.toDoubleOrNull() ?: 0.0
    val lng = longitud.toDoubleOrNull() ?: 0.0
    val distancia = if (userLat != null && userLng != null) {
        haversineKm(userLat, userLng, lat, lng)
    } else null

    return EstablecimientoUI(
        id           = id,
        nombre       = nombre,
        direccion    = direccion,
        imagenUrl    = fixImageUrl(foto_url) ?: "",
        rating       = promedio_calificacion ?: 0.0,
        totalReviews = 0,
        precioDesde  = "$0",
        latitud      = lat,
        longitud     = lng,
        distanciaKm  = distancia,
        tipoNombre   = tipo_nombre,
    )
}

private fun haversineKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371.0
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2).pow(2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2).pow(2)
    return r * 2 * atan2(sqrt(a), sqrt(1 - a))
}
