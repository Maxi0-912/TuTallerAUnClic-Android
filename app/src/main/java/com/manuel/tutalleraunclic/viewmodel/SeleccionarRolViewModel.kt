package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SeleccionarRolUiState {
    object Idle    : SeleccionarRolUiState()
    object Loading : SeleccionarRolUiState()
    data class Success(val rolNombre: String) : SeleccionarRolUiState()
    data class Error(val message: String)     : SeleccionarRolUiState()
}

@HiltViewModel
class SeleccionarRolViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<SeleccionarRolUiState>(SeleccionarRolUiState.Idle)
    val uiState: StateFlow<SeleccionarRolUiState> = _uiState.asStateFlow()

    fun asignarRol(rolId: Int) {
        viewModelScope.launch {
            _uiState.value = SeleccionarRolUiState.Loading
            repository.asignarRol(rolId)
                .onSuccess { rolNombre ->
                    _uiState.value = SeleccionarRolUiState.Success(rolNombre)
                }
                .onFailure { e ->
                    _uiState.value = SeleccionarRolUiState.Error(
                        e.message ?: "Error al asignar rol"
                    )
                }
        }
    }
}
