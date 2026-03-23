package com.manuel.tutalleraunclic.data.model.entity

import com.manuel.tutalleraunclic.data.model.entity.Rol

data class Usuario(
    val id: Int,
    val username: String,
    val email: String?,
    val telefono: String?,
    val rol: Rol?
)