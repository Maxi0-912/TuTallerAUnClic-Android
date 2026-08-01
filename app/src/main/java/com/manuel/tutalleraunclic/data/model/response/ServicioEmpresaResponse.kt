package com.manuel.tutalleraunclic.data.model.response

data class ServicioEmpresaResponse(
    val id: Int,
    val nombre: String,
    val establecimiento_id: Int,
    val establecimiento_nombre: String? = null,
    val tipo_servicio_id: Int,
    val tipo_servicio_nombre: String? = null
)
