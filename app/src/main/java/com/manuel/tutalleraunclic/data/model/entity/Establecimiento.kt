package com.manuel.tutalleraunclic.data.model.entity

import com.google.gson.annotations.SerializedName

data class Establecimiento(
    val id: Int,
    val nombre: String,
    val direccion: String,
    val telefono: String,
    val hora_apertura: String,
    val hora_cierre: String,
    val descripcion: String,
    val latitud: String,
    val longitud: String,
    val propietario: Int,
    val propietario_nombre: String? = null,
    val tipo: Int,
    val tipo_nombre: String? = null,
    val foto: String? = null,
    val foto_url: String? = null,
    val fotos_galeria: List<String>? = null,
    @SerializedName("calificacion_promedio")
    val calificacion_promedio: Double? = null,
)
