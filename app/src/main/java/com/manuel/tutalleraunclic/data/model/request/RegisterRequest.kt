package com.manuel.tutalleraunclic.data.model.request

data class RegisterRequest(
    val username: String,
    val email: String,
    val password: String
)