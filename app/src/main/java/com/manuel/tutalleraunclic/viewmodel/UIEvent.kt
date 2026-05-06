package com.manuel.tutalleraunclic.viewmodel

sealed class UiEvent {
    data class ShowMessage(val message: String) : UiEvent()
    data class ShowError(val message: String) : UiEvent()
}