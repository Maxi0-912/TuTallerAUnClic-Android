package com.manuel.tutalleraunclic.data.model.request

data class EstablecimientoRequest(
    val nombre: String,
    val direccion: String,
    val lat: Double,
    val lng: Double
)