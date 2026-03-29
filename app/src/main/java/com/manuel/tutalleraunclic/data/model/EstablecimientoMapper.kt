package com.manuel.tutalleraunclic.data.model
import com.manuel.tutalleraunclic.data.model.EstablecimientoUI
import com.manuel.tutalleraunclic.data.model.entity.Establecimiento


fun Establecimiento.toUI(): EstablecimientoUI {
    return EstablecimientoUI(
        id = id,
        nombre = nombre,
        direccion = direccion,
        imagenUrl = "", // luego lo traes del backend
        rating = calificacion, // mapeo correcto
        totalReviews = 0, // temporal si no viene del backend
        precioDesde = "$0" // temporal
    )
}