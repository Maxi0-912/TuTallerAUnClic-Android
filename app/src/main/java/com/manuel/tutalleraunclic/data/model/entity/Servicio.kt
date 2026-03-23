package com.manuel.tutalleraunclic.data.model.entity

data class Servicio(
    val id: Int,
    val nombre: String,
    val establecimiento: Int,
    val tipo_servicio: TipoServicio
)