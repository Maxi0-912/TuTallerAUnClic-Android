package com.manuel.tutalleraunclic.data.model.entity

data class Anuncio(
    val id: Int,
    val titulo: String,
    val descripcion: String? = null,
    val imagen: String? = null,
    val imagen_url: String? = null,
    val tipo: String,
    val categoria: String,
    val descuento: String? = null,
    val texto_boton: String? = null,
    val url_boton: String? = null,
    val establecimiento: Int,
    val establecimiento_nombre: String? = null,
    val fecha_inicio: String? = null,
    val fecha_fin: String? = null,

    // ── Solo lectura: nunca se envían al backend ──────────────────────────────
    val estado: String? = null,
    val motivo_rechazo: String? = null,
    val es_pago: Boolean? = null,
    val pagado: Boolean? = null,
    val requiere_pago: Boolean? = null
)
