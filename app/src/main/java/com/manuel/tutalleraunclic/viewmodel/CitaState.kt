package com.manuel.tutalleraunclic.viewmodel

import com.manuel.tutalleraunclic.data.model.response.CitaResponse

data class CitaState(
    val isLoading: Boolean = false,
    val horarios: List<String> = emptyList(),
    val citas: List<CitaResponse> = emptyList(), // 🔥 NUEVO
    val fechaSeleccionada: String = "",
    val horaSeleccionada: String? = null,
    val success: Boolean = false,
    val error: String? = null
)