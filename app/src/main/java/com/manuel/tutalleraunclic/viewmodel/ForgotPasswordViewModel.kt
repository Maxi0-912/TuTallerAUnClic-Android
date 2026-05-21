package com.manuel.tutalleraunclic.viewmodel

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor() : ViewModel() {

    sealed class UiState {
        object Idle    : UiState()
        object Loading : UiState()
        object Success : UiState()
        data class Error(val message: String) : UiState()
    }

    private val _uiState = MutableStateFlow<UiState>(UiState.Idle)
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    fun sendResetEmail(email: String) {
        if (email.isBlank()) {
            _uiState.value = UiState.Error("Ingresa tu correo electrónico")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = UiState.Error("Correo electrónico no válido")
            return
        }
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            delay(1200) // TODO: conectar al endpoint de reset del backend
            _uiState.value = UiState.Success
        }
    }

    fun resetState() { _uiState.value = UiState.Idle }
}
