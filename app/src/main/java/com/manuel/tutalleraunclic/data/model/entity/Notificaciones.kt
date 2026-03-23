package com.manuel.tutalleraunclic.data.model.entity

data class Notificacion(
    val id: Int,
    val usuario: Int,
    val titulo: String,
    val mensaje: String,
    val leida: Boolean,
    val fecha: String
)