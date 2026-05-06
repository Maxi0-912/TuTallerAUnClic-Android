package com.manuel.tutalleraunclic.viewmodel

import com.manuel.tutalleraunclic.data.model.entity.Servicio

// 🔥 STATE PROFESIONAL
sealed class ServiciosState {
    object Idle : ServiciosState()
    object Loading : ServiciosState()
    data class Success(val data: List<Servicio>) : ServiciosState()
    data class Error(val message: String) : ServiciosState()
}
