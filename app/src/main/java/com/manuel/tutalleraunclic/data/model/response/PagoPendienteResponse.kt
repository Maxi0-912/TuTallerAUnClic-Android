package com.manuel.tutalleraunclic.data.model.response

data class PagoPendienteResponse(
    val referencia: String,
    val monto: Double,
    val numero_nequi: String,
    val titular: String
)
