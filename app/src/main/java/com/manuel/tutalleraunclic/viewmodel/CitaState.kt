package com.manuel.tutalleraunclic.viewmodel

sealed class CitaState {

    // Estado inicial
    object Idle : CitaState()

    // Mientras carga (API)
    object Loading : CitaState()

    // Cuando todo sale bien
    object Success : CitaState()

    // Error con mensaje
    data class Error(val message: String) : CitaState()
}