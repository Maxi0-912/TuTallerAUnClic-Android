package com.manuel.tutalleraunclic.viewmodel

import com.manuel.tutalleraunclic.data.model.response.CitaResponse

data class CitaState(
    val fechasDisponibles: List<String> = emptyList(),
    val horarios: List<String> = emptyList(),
    val fechaSeleccionada: String = "",
    val horaSeleccionada: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)