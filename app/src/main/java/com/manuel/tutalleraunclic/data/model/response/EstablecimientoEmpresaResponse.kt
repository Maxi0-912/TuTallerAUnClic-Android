package com.manuel.tutalleraunclic.data.model.response

data class EstablecimientoEmpresaResponse(
    val id: Int,
    val nombre: String,
    val direccion: String? = null,
    val telefono: String? = null,
    val descripcion: String? = null,
    val hora_apertura: String? = null,
    val hora_cierre: String? = null,
    val latitud: String? = null,
    val longitud: String? = null,
    val tipo: Int? = null,
    val tipo_nombre: String? = null,
    val foto_url: String? = null,
    val promedio_calificacion: Double? = null
)
