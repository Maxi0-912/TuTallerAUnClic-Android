package com.manuel.tutalleraunclic.data.model.request

data class CrearCitaRequest(
    val establecimiento: Int,
    val agenda: Int,
    val fecha: String,
    val descripcion: String
)