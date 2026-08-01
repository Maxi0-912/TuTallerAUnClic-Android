package com.manuel.tutalleraunclic.data.model.response

data class CitaEmpresaResponse(
    val id: Int = 0,
    val usuario_nombre: String = "",
    val usuario_telefono: String? = null,
    val establecimiento_id: Int = 0,
    val establecimiento_nombre: String? = null,
    val fecha: String = "",
    val hora: String = "",
    val estado: String = "",
    val servicio_nombre: String? = null,
    val servicio_texto: String? = null,
    val vehiculo_placa: String? = null,
    val descripcion: String? = null,
    val comentario_empresa: String? = null,
)
