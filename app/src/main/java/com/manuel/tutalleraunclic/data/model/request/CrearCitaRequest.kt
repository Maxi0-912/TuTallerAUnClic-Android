package com.manuel.tutalleraunclic.data.model.request

data class CrearCitaRequest(
    val cliente_id: Int,
    val taller_id: Int,
    val fecha: String,
    val hora: String,
    val servicio: String
)