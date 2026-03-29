package com.manuel.tutalleraunclic.data.model

data class EstablecimientoUI(
    val id: Int,
    val nombre: String,
    val imagenUrl: String,
    val rating: Double,
    val totalReviews: Int,
    val direccion: String,
    val precioDesde: String
)