package com.manuel.tutalleraunclic.data.model.entity

import com.manuel.tutalleraunclic.data.model.entity.TipoEstablecimiento

data class Establecimiento(

    val id: Int,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val hora_apertura: String,
    val hora_cierre: String,
    val descripcion: String,
    val latitud: String,   // 🔥 viene como string
    val longitud: String,  // 🔥 viene como string
    val propietario: Int,  // 🔥 ID
    val tipo: Int          // 🔥 ID
)