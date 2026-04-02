package com.manuel.tutalleraunclic.viewmodel

import com.manuel.tutalleraunclic.data.model.entity.Agenda

data class CitaState(
    val fechaSeleccionada: String = "",
    val horaSeleccionada: String? = null, // 🔥 ESTA LÍNEA
    val agendas: List<Agenda> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)