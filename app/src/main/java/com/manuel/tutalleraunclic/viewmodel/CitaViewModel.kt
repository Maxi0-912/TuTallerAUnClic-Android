package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.request.CrearCitaRequest
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CitasViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _mensaje = MutableStateFlow("")
    val mensaje: StateFlow<String> = _mensaje

    private val _error = MutableStateFlow("")
    val error: StateFlow<String> = _error

    fun crearCita(
        servicioId: Int,
        establecimientoId: Int,
        vehiculo: String,
        fecha: String,
        hora: String
    ) {

        viewModelScope.launch {

            _loading.value = true

            try {

                val request = CrearCitaRequest(
                    servicio = servicioId,
                    establecimiento = establecimientoId,
                    agenda = 1,
                    vehiculo = vehiculo,
                    fecha = fecha,
                    hora = hora
                )

                repository.crearCita(request)

                _mensaje.value = "Cita creada correctamente"

            } catch (e: Exception) {
                _error.value = e.message ?: "Error al crear cita"
            } finally {
                _loading.value = false
            }
        }
    }
}