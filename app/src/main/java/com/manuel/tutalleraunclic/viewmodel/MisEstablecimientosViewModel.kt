package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.response.EstablecimientoEmpresaResponse
import com.manuel.tutalleraunclic.data.repository.EstablecimientoEmpresaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MisEstablecimientosUiState(
    val isLoading: Boolean = false,
    val establecimientos: List<EstablecimientoEmpresaResponse> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class MisEstablecimientosViewModel @Inject constructor(
    private val repository: EstablecimientoEmpresaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MisEstablecimientosUiState())
    val uiState: StateFlow<MisEstablecimientosUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    fun cargarEstablecimientos() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getMisEstablecimientos()
                .onSuccess { lista -> _uiState.update { it.copy(isLoading = false, establecimientos = lista) } }
                .onFailure { e ->
                    val msg = e.message ?: "Error al cargar tus establecimientos"
                    _uiState.update { it.copy(isLoading = false, error = msg) }
                }
        }
    }

    fun eliminarEstablecimiento(id: Int) {
        viewModelScope.launch {
            repository.eliminar(id)
                .onSuccess {
                    _uiState.update { state -> state.copy(establecimientos = state.establecimientos.filterNot { it.id == id }) }
                    _uiEvent.emit(UiEvent.ShowMessage("Establecimiento eliminado"))
                }
                .onFailure { e ->
                    _uiEvent.emit(UiEvent.ShowError(e.message ?: "No se pudo eliminar el establecimiento"))
                }
        }
    }
}
