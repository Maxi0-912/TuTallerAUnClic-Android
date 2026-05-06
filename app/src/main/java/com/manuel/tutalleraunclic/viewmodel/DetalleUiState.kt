package com.manuel.tutalleraunclic.viewmodel

import com.manuel.tutalleraunclic.data.model.entity.Calificacion
import com.manuel.tutalleraunclic.data.model.entity.Establecimiento
import com.manuel.tutalleraunclic.data.model.entity.Servicio

data class DetalleUiState(
    val establecimiento: Establecimiento? = null,
    val servicios: List<Servicio> = emptyList(),
    val resenas: List<Calificacion> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)
