package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.request.CrearCitaRequest
import com.manuel.tutalleraunclic.data.repository.CitaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.manuel.tutalleraunclic.viewmodel.CitasState

@HiltViewModel
class MisCitasViewModel @Inject constructor(
    private val repository: CitaRepository
) : ViewModel() {

    private val _state = MutableStateFlow<CitasState>(CitasState.Loading)
    val state: StateFlow<CitasState> = _state

    init {
        obtenerCitas() // 🔥 carga automática (pro)
    }

    fun obtenerCitas() {
        viewModelScope.launch {
            _state.value = CitasState.Loading

            try {
                val citas = repository.getMisCitas() // ⚠️ usa un solo nombre consistente
                _state.value = CitasState.Success(citas)

            } catch (e: Exception) {
                _state.value = CitasState.Error(
                    e.message ?: "Error al cargar citas"
                )
            }
        }
    }

    fun eliminarCita(id: Int) {
        viewModelScope.launch {
            try {
                repository.eliminarCita(id)

                // 🔥 actualización inteligente (sin loading)
                val currentState = _state.value
                if (currentState is CitasState.Success) {
                    val nuevaLista = currentState.citas.filter { it.id != id }
                    _state.value = CitasState.Success(nuevaLista)
                }

            } catch (e: Exception) {
                _state.value = CitasState.Error(
                    e.message ?: "Error al eliminar cita"
                )
            }
        }
    }

    fun editarCita(id: Int, request: CrearCitaRequest) {
        viewModelScope.launch {
            try {
                repository.editarCita(id, request)

                // 🔥 refresco (puedes optimizar luego)
                obtenerCitas()

            } catch (e: Exception) {
                _state.value = CitasState.Error(
                    e.message ?: "Error al editar cita"
                )
            }
        }
    }

    // 🔥 retry PRO
    fun retry() {
        obtenerCitas()
    }
}