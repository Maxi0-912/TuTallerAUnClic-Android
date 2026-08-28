package com.manuel.tutalleraunclic.data.model.request

data class RegistrarDispositivoRequest(
    val token: String,
    val plataforma: String = "android"
)
