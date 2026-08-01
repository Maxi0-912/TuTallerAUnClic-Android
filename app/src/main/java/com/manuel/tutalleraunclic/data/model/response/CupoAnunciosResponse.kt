package com.manuel.tutalleraunclic.data.model.response

data class CupoAnunciosResponse(
    val cupo_gratis: Int,
    val usados: Int,
    val gratis_restantes: Int,
    val proximo_requiere_pago: Boolean
)
