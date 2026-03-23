package com.manuel.tutalleraunclic.data.model.entity

data class PrestacionServicio(
    val id: Int,
    val establecimiento: Int,
    val agenda: Int,
    val vehiculo: String,
    val usuario: Int,
    val servicio: Int,
    val fecha: String,
    val estado: String
)