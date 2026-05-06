package com.manuel.tutalleraunclic.data.model.response

data class CitaResponse(
    val id: Int = 0,
    val fecha: String = "",
    val hora: String = "",
    val estado: String = "",
    val establecimiento: Int = 0,
    val establecimiento_nombre: String? = null,
    val servicio: Int = 0,
    val servicio_nombre: String? = null,
    val servicio_texto: String? = null,
    val vehiculo_placa: String? = null,
    val tiene_resena: Boolean = false,
    val descripcion: String? = null,
)
