package com.manuel.tutalleraunclic.data.model.entity

data class Dashboard(
    val total_citas: Int,
    val citas_pendientes: Int,
    val citas_confirmadas: Int,
    val citas_finalizadas: Int,
    val citas_canceladas: Int
)