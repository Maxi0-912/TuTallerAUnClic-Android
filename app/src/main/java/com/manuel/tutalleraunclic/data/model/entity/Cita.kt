package com.manuel.tutalleraunclic.data.model.entity

import com.manuel.tutalleraunclic.data.model.entity.Servicio

data class Cita(

    val id: Int,
    val fecha: String,
    val servicio: Servicio,
    val vehiculo: Vehiculo,
    val establecimientoID: Int,
    val ServicioId: Int,

)