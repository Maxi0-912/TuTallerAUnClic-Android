package com.manuel.tutalleraunclic.data.model.entity

import com.manuel.tutalleraunclic.data.model.entity.Servicio

data class Cita(
    val id: Int,
    val fecha: String,
    val hora: String,
    val servicio: String,
    val establecimiento: String,
    val estado: String
)