package com.manuel.tutalleraunclic.viewmodel

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.R
import com.manuel.tutalleraunclic.data.model.request.ActualizarCitaRequest
import com.manuel.tutalleraunclic.data.repository.CitaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MisCitasViewModel @Inject constructor(
    private val repository: CitaRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow<CitasState>(CitasState.Loading)
    val state: StateFlow<CitasState> = _state

    private var estadosAnteriores: Map<Int, String> = emptyMap()

    init {
        obtenerCitas()
    }

    fun obtenerCitas() {
        viewModelScope.launch {
            _state.value = CitasState.Loading

            repository.getMisCitas()
                .onSuccess { citas ->
                    citas.forEach { cita ->
                        val prev = estadosAnteriores[cita.id]
                        if (prev != null && prev != cita.estado) {
                            notificarCambioEstado(cita.id, cita.estado)
                        }
                    }
                    estadosAnteriores = citas.associate { it.id to it.estado }
                    _state.value = CitasState.Success(citas)
                }
                .onFailure { e ->
                    _state.value = CitasState.Error(e.message ?: "Error al cargar citas")
                }
        }
    }

    fun eliminarCita(id: Int) {
        viewModelScope.launch {
            try {
                repository.eliminarCita(id)
                val currentState = _state.value
                if (currentState is CitasState.Success) {
                    _state.value = CitasState.Success(currentState.citas.filter { it.id != id })
                }
            } catch (e: Exception) {
                _state.value = CitasState.Error(e.message ?: "Error al eliminar cita")
            }
        }
    }

    fun editarCita(id: Int, request: ActualizarCitaRequest) {
        viewModelScope.launch {
            try {
                repository.editarCita(id, request)
                obtenerCitas()
            } catch (e: Exception) {
                _state.value = CitasState.Error(e.message ?: "Error al editar cita")
            }
        }
    }

    fun retry() {
        obtenerCitas()
    }

    private fun notificarCambioEstado(citaId: Int, nuevoEstado: String) {
        val label = nuevoEstado.replaceFirstChar { it.uppercase() }
        val notification = NotificationCompat.Builder(context, "citas")
            .setSmallIcon(R.drawable.logo_solo)
            .setContentTitle("Estado de tu cita actualizado")
            .setContentText("Tu cita cambió a $label")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(citaId + 2000, notification)
    }
}
