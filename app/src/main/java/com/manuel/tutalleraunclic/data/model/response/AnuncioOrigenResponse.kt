package com.manuel.tutalleraunclic.data.model.response

data class AnuncioOrigenResponse(
    val id: Int,
    val titulo: String,
    val descuento: String? = null
)
