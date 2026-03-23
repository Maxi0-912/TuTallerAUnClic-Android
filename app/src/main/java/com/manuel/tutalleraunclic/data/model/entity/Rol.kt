package com.manuel.tutalleraunclic.data.model.entity

data class Rol(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val activo: Boolean,
    val fecha_creacion: String,
    val fecha_actualizacion: String
)