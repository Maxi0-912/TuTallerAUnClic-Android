package com.manuel.tutalleraunclic.data.model.request



data class ActualizarCitaRequest(
    val descripcion: String? = null,
    val establecimiento: Int? = null,
    val servicio: Int? = null,
    val fecha: String? = null,
    val hora: String? = null,
    val placa: String? = null
)