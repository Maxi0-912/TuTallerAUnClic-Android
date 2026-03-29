package com.manuel.tutalleraunclic.data.model.entity

data class Establecimiento(

    val id: Int, // 🔥 CORRECTO
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val hora_apertura: String,
    val hora_cierre: String,
    val descripcion: String,
    val latitud: String,
    val longitud: String,
    val propietario: Int,
    val tipo: Int,
    val calificacion: Double,
)