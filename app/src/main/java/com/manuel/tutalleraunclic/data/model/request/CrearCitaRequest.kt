package com.manuel.tutalleraunclic.data.model.request

data class CrearCitaRequest(
    val establecimiento: Int,
    val agenda: Int,
    val vehiculo: String,
    val servicio: Int,
    val fecha: String,
    val hora: String
)