package com.manuel.tutalleraunclic.data.model.request

data class UpdateUserRequest(
    val username: String,
    val first_name: String,
    val last_name: String,
    val email: String,
    val telefono: String
)