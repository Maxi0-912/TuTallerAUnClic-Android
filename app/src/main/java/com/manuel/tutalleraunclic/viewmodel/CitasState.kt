package com.manuel.tutalleraunclic.viewmodel

import com.manuel.tutalleraunclic.data.model.response.CitaResponse

sealed class CitasState {

    object Loading : CitasState()

    data class Success(
        val citas: List<CitaResponse>
    ) : CitasState()

    data class Error(
        val mensaje: String
    ) : CitasState()
}

