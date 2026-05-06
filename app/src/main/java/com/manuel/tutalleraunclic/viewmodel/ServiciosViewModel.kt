package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.entity.Servicio
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.manuel.tutalleraunclic.viewmodel.ServiciosState


@HiltViewModel
class ServiciosViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _state = MutableStateFlow<ServiciosState>(ServiciosState.Idle)
    val state: StateFlow<ServiciosState> = _state

    fun cargarServicios(establecimientoId: Int) {

        viewModelScope.launch {

            _state.value = ServiciosState.Loading

            try {
                val servicios = repository.getServicios(establecimientoId)

                _state.value = ServiciosState.Success(servicios)

            } catch (e: Exception) {
                _state.value = ServiciosState.Error(
                    e.message ?: "Error desconocido"
                )
            }
        }
    }
}