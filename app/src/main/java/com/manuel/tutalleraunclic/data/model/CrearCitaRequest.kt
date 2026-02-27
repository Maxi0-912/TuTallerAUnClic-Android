package com.manuel.tutalleraunclic.data.model

data class CrearCitaRequest(
    val fecha: String,
    val hora: String,
    val descripcion: String,
    val servicio_id: Int
)