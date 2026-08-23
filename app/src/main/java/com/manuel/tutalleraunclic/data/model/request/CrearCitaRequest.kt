package com.manuel.tutalleraunclic.data.model.request

import com.google.gson.annotations.SerializedName

data class CrearCitaRequest(
    val establecimiento: Int,
    val fecha: String,
    val hora: String,
    val servicio: Int,
    val placa: String,
    val descripcion: String? = null,
    @SerializedName("anuncio_origen_id")
    val anuncioOrigenId: Int? = null,
)
