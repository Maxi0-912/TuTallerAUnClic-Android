package com.manuel.tutalleraunclic.data.model.entity

import com.manuel.tutalleraunclic.data.model.entity.Servicio

data class Cita(
    val id: Int,
    val fecha: String,
    val hora: String,
    val descripcion: String?, // 👈 AGREGA ESTO
    val establecimiento: Int,
    val servicio: Int,
    val estado: String
)