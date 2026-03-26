package com.manuel.tutalleraunclic.data.model
import com.manuel.tutalleraunclic.data.model.EstablecimientoUI
import com.manuel.tutalleraunclic.data.model.entity.Establecimiento


fun Establecimiento.toUI(): EstablecimientoUI {
    return EstablecimientoUI(
        id = id,
        nombre = nombre,
        direccion = direccion,
        calificacion = calificacion,
        imagen = imagen
    )
}