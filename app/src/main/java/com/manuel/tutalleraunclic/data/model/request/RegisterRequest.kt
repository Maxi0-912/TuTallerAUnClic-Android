package com.manuel.tutalleraunclic.data.model.request

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String,
    val rol: Int = 2,
    val first_name: String = "",
    val last_name: String = "",
    val telefono: String = ""
)