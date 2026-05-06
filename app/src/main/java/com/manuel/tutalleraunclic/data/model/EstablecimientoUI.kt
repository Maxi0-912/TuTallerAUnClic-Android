package com.manuel.tutalleraunclic.data.model

data class EstablecimientoUI(
    val id: Int,
    val nombre: String,
    val imagenUrl: String,
    val rating: Double,
    val totalReviews: Int,
    val direccion: String,
    val precioDesde: String,
    val latitud: Double = 0.0,
    val longitud: Double = 0.0,
    val distanciaKm: Double? = null,
    val tipoNombre: String? = null,
) {
    val tipo: TipoEstablecimiento get() = TipoEstablecimiento.from(tipoNombre)
}
