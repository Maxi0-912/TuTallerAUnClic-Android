package com.manuel.tutalleraunclic.data.model.request

data class CrearCitaRequest(
    val establecimiento: Int,
    val fecha: String,
    val hora: String,
    val servicio: Int,
    val placa: String,
    val descripcion: String? = null,
)
