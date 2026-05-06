package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.response.CitaResponse
import com.manuel.tutalleraunclic.data.repository.CitaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CitaListViewModel @Inject constructor(
    private val repository: CitaRepository
) : ViewModel() {

    // ---------------- STATE ----------------

    private val _state = MutableStateFlow<CitasState>(CitasState.Loading)
    val state: StateFlow<CitasState> = _state

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent

    private val deletingIds = mutableSetOf<Int>()

    // ---------------- GET CITAS ----------------

    fun obtenerCitas() {
        viewModelScope.launch {
            _state.value = CitasState.Loading

            repository.getMisCitas()
                .onSuccess { citas ->
                    _state.value = CitasState.Success(citas)
                }
                .onFailure {
                    _state.value = CitasState.Error("Error al cargar citas")
                }
        }
    }

    // ---------------- DELETE ----------------

    fun eliminarCita(id: Int) {
        if (deletingIds.contains(id)) return

        viewModelScope.launch {

            val current = _state.value
            if (current !is CitasState.Success) return@launch

            deletingIds.add(id)

            val listaActual = current.citas
            val nuevaLista = listaActual.filter { it.id != id }

            // 🔥 Optimistic UI (pro)
            _state.value = CitasState.Success(nuevaLista)

            repository.eliminarCita(id)
                .onSuccess {
                    emitirMensaje("Cita eliminada")
                }
                .onFailure { e ->
                    // rollback
                    _state.value = CitasState.Success(listaActual)

                    val msg = e.message ?: "Error al eliminar"
                    emitirError(msg)
                }

            deletingIds.remove(id)
        }
    }

    // ---------------- HELPERS ----------------

    private fun emitirError(msg: String) {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowError(msg))
        }
    }

    private fun emitirMensaje(msg: String) {
        viewModelScope.launch {
            _uiEvent.emit(UiEvent.ShowMessage(msg))
        }
    }
}