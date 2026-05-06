package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.request.UpdateUserRequest
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    // ---------------- STATE ----------------

    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    // ---------------- ACTUALIZAR PERFIL ----------------

    fun actualizarPerfil(data: UpdateUserRequest) {
        viewModelScope.launch {

            _uiState.update { it.copy(isLoading = true) }

            repository.actualizarPerfil(data)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, success = true) }
                    _uiEvent.emit(UiEvent.ShowMessage("Perfil actualizado"))
                }
                .onFailure { e ->
                    val msg = e.message ?: "Error al actualizar"
                    _uiState.update { it.copy(isLoading = false) }
                    _uiEvent.emit(UiEvent.ShowError(msg))
                }
        }
    }

    // ---------------- RESET ----------------

    fun resetState() {
        _uiState.value = MainUiState()
    }
}