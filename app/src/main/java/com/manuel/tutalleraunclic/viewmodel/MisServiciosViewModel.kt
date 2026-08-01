package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.response.EstablecimientoEmpresaResponse
import com.manuel.tutalleraunclic.data.model.response.ServicioEmpresaResponse
import com.manuel.tutalleraunclic.data.repository.EstablecimientoEmpresaRepository
import com.manuel.tutalleraunclic.data.repository.ServicioEmpresaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MisServiciosUiState(
    val isLoading: Boolean = false,
    val servicios: List<ServicioEmpresaResponse> = emptyList(),
    val establecimientos: List<EstablecimientoEmpresaResponse> = emptyList(),
    val filtroEstablecimientoId: Int? = null,
    val busqueda: String = "",
    val error: String? = null
)

@HiltViewModel
class MisServiciosViewModel @Inject constructor(
    private val servicioRepository: ServicioEmpresaRepository,
    private val establecimientoRepository: EstablecimientoEmpresaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MisServiciosUiState())
    val uiState: StateFlow<MisServiciosUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    fun cargar() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val establecimientos = establecimientoRepository.getMisEstablecimientos().getOrDefault(emptyList())

            servicioRepository.getServicios()
                .onSuccess { lista ->
                    _uiState.update { it.copy(isLoading = false, servicios = lista, establecimientos = establecimientos) }
                }
                .onFailure { e ->
                    val msg = e.message ?: "Error al cargar los servicios"
                    _uiState.update { it.copy(isLoading = false, establecimientos = establecimientos, error = msg) }
                }
        }
    }

    fun setFiltroEstablecimiento(id: Int?) =
        _uiState.update { it.copy(filtroEstablecimientoId = id) }

    fun setBusqueda(texto: String) =
        _uiState.update { it.copy(busqueda = texto) }

    fun eliminarServicio(id: Int) {
        viewModelScope.launch {
            servicioRepository.eliminar(id)
                .onSuccess {
                    _uiState.update { state -> state.copy(servicios = state.servicios.filterNot { it.id == id }) }
                    _uiEvent.emit(UiEvent.ShowMessage("Servicio eliminado"))
                }
                .onFailure { e ->
                    _uiEvent.emit(UiEvent.ShowError(e.message ?: "No se pudo eliminar el servicio"))
                }
        }
    }
}
