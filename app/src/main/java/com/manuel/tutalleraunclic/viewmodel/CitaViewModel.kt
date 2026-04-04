package com.manuel.tutalleraunclic.viewmodel

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.request.CrearCitaRequest
import com.manuel.tutalleraunclic.viewmodel.CitasState
import com.manuel.tutalleraunclic.data.repository.CitaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class CitaViewModel @Inject constructor(
    private val repository: CitaRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(CitaState())
    val state: StateFlow<CitaState> = _state

    // =========================
    // 🔥 HORARIOS DINÁMICOS
    // =========================
    fun cargarHorarios(establecimientoId: Int, fecha: String) {
        viewModelScope.launch {

            _state.value = _state.value.copy(isLoading = true, error = null)

            try {
                val horarios = repository.getHorarios(establecimientoId, fecha)

                _state.value = _state.value.copy(
                    isLoading = false,
                    horarios = horarios
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error cargando horarios"
                )
            }
        }
    }

    // =========================
    // 📅 FECHA
    // =========================
    fun seleccionarFecha(fecha: String, establecimientoId: Int) {
        _state.value = _state.value.copy(
            fechaSeleccionada = fecha,
            horaSeleccionada = null
        )
        cargarHorarios(establecimientoId, fecha)
    }

    // =========================
    // ⏰ HORA
    // =========================
    fun seleccionarHora(hora: String) {
        _state.value = _state.value.copy(horaSeleccionada = hora)
    }

    // =========================
    // 🚀 CREAR CITA
    // =========================
    fun crearCita(
        establecimientoId: Int,
        servicioId: Int,
        descripcion: String
    ) {
        val currentState = _state.value

        if (currentState.horaSeleccionada == null) {
            _state.value = currentState.copy(error = "Selecciona una hora")
            return
        }

        viewModelScope.launch {
            try {
                repository.crearCita(
                    CrearCitaRequest(
                        establecimiento = establecimientoId,
                        fecha = currentState.fechaSeleccionada,
                        hora = currentState.horaSeleccionada,
                        servicio = servicioId,
                        descripcion = descripcion
                    )
                )

                notificarCita()

                // 🔥 REFRESH AUTOMÁTICO
                cargarMisCitas()

                _state.value = currentState.copy(
                    success = true,
                    error = null
                )

            } catch (e: HttpException) {
                val error = e.response()?.errorBody()?.string()

                _state.value = currentState.copy(
                    error = error ?: "Error al crear cita"
                )

            } catch (e: Exception) {
                _state.value = currentState.copy(
                    error = "Error inesperado"
                )
            }
        }
    }

    // =========================
    // 📋 MIS CITAS
    // =========================
    fun cargarMisCitas() {
        viewModelScope.launch {

            _state.value = _state.value.copy(isLoading = true)

            try {
                val citas = repository.getMisCitas()

                _state.value = _state.value.copy(
                    isLoading = false,
                    citas = citas
                )

            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Error cargando citas"
                )
            }
        }
    }

    fun eliminarCita(id: Int, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = repository.eliminarCita(id)

            result
                .onSuccess {
                    val current = _state.value
                    if (current is CitasState.Success) {
                        val nuevaLista = current.citas.filter { it.id != id }
                        _state.value = CitasState.Success(nuevaLista)
                    }
                }
                .onFailure {
                    onError(it.message ?: "Error al eliminar")
                }
        }
    }

    // =========================
    // 🔔 NOTIFICACIÓN
    // =========================
    private fun notificarCita() {
        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val notification = NotificationCompat.Builder(context, "citas")
            .setContentTitle("Cita agendada 🚗")
            .setContentText("Todo listo, revisa tus citas")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        manager.notify(1, notification)
    }
}