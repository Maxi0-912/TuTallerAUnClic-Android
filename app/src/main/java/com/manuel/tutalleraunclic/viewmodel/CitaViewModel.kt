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

    private val _state = MutableStateFlow<CitaState>(CitaState.Idle)
    val state: StateFlow<CitaState> = _state

    fun crearCita(
        establecimientoId: Int,
        servicioId: Int,
        fecha: String,
        hora: String,
        comentario: String
    ) {

        if (fecha.isBlank() || hora.isBlank()) {
            _state.value = CitaState.Error("Fecha y hora son obligatorias")
            return
        }

        viewModelScope.launch {
            _state.value = CitaState.Loading

            try {
                repository.crearCita(
                    establecimientoId,
                    servicioId,
                    fecha,
                    hora,
                    comentario
                )

                _state.value = CitaState.Success

                // 🔔 Notificación automática
                notificarCita()

            } catch (e: Exception) {
                _state.value = CitaState.Error(
                    e.message ?: "Error al crear cita"
                )
            }
        }
    }

    fun cargarAgendas(establecimientoId: Int, fecha: String) {
        viewModelScope.launch {

            isLoadingAgendas = true

            try {
                agendas = repository.obtenerAgendas(establecimientoId, fecha)
            } catch (e: Exception) {
                setError("Error cargando horarios")
            }

            isLoadingAgendas = false
        }
    }

    fun resetState() {
        _state.value = CitaState.Idle
    }

    // 🔔 NOTIFICACIÓN PRO
    private fun notificarCita() {

        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("destino", "citas")
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, "citas")
            .setContentTitle("Cita agendada 🚗")
            .setContentText("Tu cita fue creada correctamente")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(1, notification)
    }
    fun setError(message: String) {
        _state.value = CitaState.Error(message)
    }
}