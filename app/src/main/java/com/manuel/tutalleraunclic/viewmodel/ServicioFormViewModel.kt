package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.entity.TipoServicio
import com.manuel.tutalleraunclic.data.model.response.EstablecimientoEmpresaResponse
import com.manuel.tutalleraunclic.data.repository.EstablecimientoEmpresaRepository
import com.manuel.tutalleraunclic.data.repository.ServicioEmpresaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ServicioFormUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val esEdicion: Boolean = false,
    val servicioId: Int? = null,

    val establecimientos: List<EstablecimientoEmpresaResponse> = emptyList(),
    val establecimientoId: Int? = null,

    val tiposServicio: List<TipoServicio> = emptyList(),
    val tipoServicioId: Int? = null,
    val creandoTipo: Boolean = false,

    val nombre: String = "",

    val guardadoExitoso: Boolean = false
)

@HiltViewModel
class ServicioFormViewModel @Inject constructor(
    private val servicioRepository: ServicioEmpresaRepository,
    private val establecimientoRepository: EstablecimientoEmpresaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ServicioFormUiState())
    val uiState: StateFlow<ServicioFormUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    private var yaInicializado = false

    fun inicializar(servicioId: Int?) {
        if (yaInicializado) return
        yaInicializado = true
        _uiState.update { it.copy(esEdicion = servicioId != null, servicioId = servicioId) }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val establecimientos = establecimientoRepository.getMisEstablecimientos().getOrDefault(emptyList())
            val tipos = servicioRepository.getTipos().getOrDefault(emptyList())
            _uiState.update { it.copy(establecimientos = establecimientos, tiposServicio = tipos) }

            if (servicioId != null) {
                val servicio = servicioRepository.getServicios().getOrNull()
                    ?.firstOrNull { it.id == servicioId }

                if (servicio != null) {
                    _uiState.update {
                        it.copy(
                            nombre = servicio.nombre,
                            establecimientoId = servicio.establecimiento_id,
                            tipoServicioId = servicio.tipo_servicio_id
                        )
                    }
                } else {
                    _uiEvent.emit(UiEvent.ShowError("No se pudo cargar el servicio"))
                }
            } else if (establecimientos.size == 1) {
                _uiState.update { it.copy(establecimientoId = establecimientos.first().id) }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun seleccionarEstablecimiento(id: Int) =
        _uiState.update { it.copy(establecimientoId = id, tipoServicioId = null) }

    fun seleccionarTipoServicio(id: Int) =
        _uiState.update { it.copy(tipoServicioId = id) }

    fun setNombre(v: String) = _uiState.update { it.copy(nombre = v) }

    fun crearTipoNuevo(nombre: String) {
        if (nombre.isBlank() || _uiState.value.creandoTipo) return
        viewModelScope.launch {
            _uiState.update { it.copy(creandoTipo = true) }
            servicioRepository.crearTipo(nombre.trim())
                .onSuccess { tipo ->
                    _uiState.update {
                        it.copy(
                            creandoTipo = false,
                            tiposServicio = it.tiposServicio + tipo,
                            tipoServicioId = tipo.id
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(creandoTipo = false) }
                    _uiEvent.emit(UiEvent.ShowError(e.message ?: "No se pudo crear el tipo de servicio"))
                }
        }
    }

    fun guardar() {
        val state = _uiState.value

        if (state.nombre.isBlank() || state.establecimientoId == null || state.tipoServicioId == null) {
            viewModelScope.launch { _uiEvent.emit(UiEvent.ShowError("Todos los campos son obligatorios")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val result = if (state.esEdicion) {
                servicioRepository.editar(state.servicioId!!, state.nombre.trim(), state.establecimientoId, state.tipoServicioId)
            } else {
                servicioRepository.crear(state.nombre.trim(), state.establecimientoId, state.tipoServicioId)
            }

            result
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, guardadoExitoso = true) }
                    _uiEvent.emit(UiEvent.ShowMessage(if (state.esEdicion) "Cambios guardados" else "Servicio creado"))
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false) }
                    _uiEvent.emit(UiEvent.ShowError(e.message ?: "No se pudo guardar el servicio"))
                }
        }
    }
}
