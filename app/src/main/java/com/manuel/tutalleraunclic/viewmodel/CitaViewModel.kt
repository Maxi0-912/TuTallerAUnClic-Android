package com.manuel.tutalleraunclic.viewmodel

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.MainActivity
import com.manuel.tutalleraunclic.data.repository.MainRepository
import  com.manuel.tutalleraunclic.data.model.request.CrearCitaRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CitaViewModel @Inject constructor(
    private val repository: MainRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(CitaState())
    val state: StateFlow<CitaState> = _state

    fun seleccionarFecha(fecha: String, establecimientoId: Int) {
        _state.value = _state.value.copy(
            fechaSeleccionada = fecha,

        )
        cargarAgendas(establecimientoId, fecha)
    }



    fun seleccionarHora(hora: String) {
        _state.value = _state.value.copy(
            horaSeleccionada = hora
        )
    }

    fun cargarAgendas(establecimientoId: Int, fecha: String) {
        viewModelScope.launch {

            _state.value = _state.value.copy(isLoading = true)

            try {
                val agendas = repository.obtenerAgendas(establecimientoId, fecha)

                _state.value = _state.value.copy(
                    isLoading = false,
                    agendas = agendas,
                    error = null
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error cargando horarios"
                )
            }
        }
    }

    fun crearCita(
        establecimientoId: Int,
        descripcion: String
    ) {
        val state = _state.value

        if (state.horaSeleccionada == null) {
            _state.value = state.copy(error = "Selecciona una hora")
            return
        }

        viewModelScope.launch {
            try {
                repository.crearCita(
                    CrearCitaRequest(
                        establecimiento = establecimientoId,
                        fecha = state.fechaSeleccionada,
                        hora = state.horaSeleccionada,
                        servicio = 1,
                        descripcion = descripcion
                    )
                )

                _state.value = state.copy(success = true)

            } catch (e: Exception) {
                _state.value = state.copy(error = "Error al crear cita")
            }
        }
    }

    private fun notificarCita() {
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, "citas")
            .setContentTitle("Cita agendada 🚗")
            .setContentText("Todo listo, revisa tus citas")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        manager.notify(1, notification)
    }
}