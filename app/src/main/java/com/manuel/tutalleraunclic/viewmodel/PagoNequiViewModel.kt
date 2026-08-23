package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.response.PagoPendienteResponse
import com.manuel.tutalleraunclic.data.repository.AnuncioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class PagoNequiUiState {
    data object Cargando : PagoNequiUiState()
    data class Exito(val pago: PagoPendienteResponse) : PagoNequiUiState()
    data class Error(val mensaje: String) : PagoNequiUiState()
}

@HiltViewModel
class PagoNequiViewModel @Inject constructor(
    private val anuncioRepository: AnuncioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PagoNequiUiState>(PagoNequiUiState.Cargando)
    val uiState: StateFlow<PagoNequiUiState> = _uiState.asStateFlow()

    private var anuncioIdCargado: Int? = null

    /** Genera (o recupera) el PagoPendiente para este anuncio en el backend. */
    fun cargar(anuncioId: Int) {
        if (anuncioIdCargado == anuncioId && _uiState.value is PagoNequiUiState.Exito) return
        anuncioIdCargado = anuncioId

        viewModelScope.launch {
            _uiState.value = PagoNequiUiState.Cargando
            anuncioRepository.crearPagoPendiente(anuncioId)
                .onSuccess { pago -> _uiState.value = PagoNequiUiState.Exito(pago) }
                .onFailure { e ->
                    _uiState.value = PagoNequiUiState.Error(e.message ?: "No se pudo generar el pago")
                }
        }
    }
}
