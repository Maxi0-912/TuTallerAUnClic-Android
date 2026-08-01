package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.response.CitaEmpresaResponse
import com.manuel.tutalleraunclic.data.repository.CitaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CitasEmpresaUiState(
    val isLoading: Boolean = false,
    val citas: List<CitaEmpresaResponse> = emptyList(),
    val filtroEstado: String? = null,           // null = "todas"
    val filtroEstablecimientoId: Int? = null,   // null = "todos"
    val busqueda: String = "",
    val accionEnProgreso: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class CitasEmpresaViewModel @Inject constructor(
    private val repository: CitaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CitasEmpresaUiState())
    val uiState: StateFlow<CitasEmpresaUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    fun cargarCitas() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getCitasEmpresa()
                .onSuccess { lista -> _uiState.update { it.copy(isLoading = false, citas = lista) } }
                .onFailure { e ->
                    val msg = e.message ?: "Error al cargar las citas"
                    _uiState.update { it.copy(isLoading = false, error = msg) }
                }
        }
    }

    fun setFiltroEstado(estado: String?) =
        _uiState.update { it.copy(filtroEstado = if (it.filtroEstado == estado) null else estado) }

    fun setFiltroEstablecimiento(id: Int?) =
        _uiState.update { it.copy(filtroEstablecimientoId = id) }

    fun setBusqueda(texto: String) =
        _uiState.update { it.copy(busqueda = texto) }

    fun cambiarEstado(id: Int, estado: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(accionEnProgreso = true) }
            repository.cambiarEstadoCita(id, estado)
                .onSuccess {
                    _uiState.update { it.copy(accionEnProgreso = false) }
                    _uiEvent.emit(UiEvent.ShowMessage("Estado actualizado"))
                    cargarCitas()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(accionEnProgreso = false) }
                    _uiEvent.emit(UiEvent.ShowError(e.message ?: "No se pudo cambiar el estado"))
                }
        }
    }

    fun agregarComentario(id: Int, comentario: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(accionEnProgreso = true) }
            repository.comentarCita(id, comentario)
                .onSuccess {
                    _uiState.update { it.copy(accionEnProgreso = false) }
                    _uiEvent.emit(UiEvent.ShowMessage("Comentario guardado"))
                    cargarCitas()
                }
                .onFailure { e ->
                    _uiState.update { it.copy(accionEnProgreso = false) }
                    _uiEvent.emit(UiEvent.ShowError(e.message ?: "No se pudo guardar el comentario"))
                }
        }
    }
}
