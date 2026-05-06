package com.manuel.tutalleraunclic.viewmodel

import android.app.NotificationManager
import android.content.Context
import androidx.compose.runtime.*
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.R
import com.manuel.tutalleraunclic.data.model.entity.Servicio
import com.manuel.tutalleraunclic.data.model.request.ActualizarCitaRequest
import com.manuel.tutalleraunclic.data.model.request.CrearCitaRequest
import com.manuel.tutalleraunclic.data.model.response.CitaResponse
import com.manuel.tutalleraunclic.data.repository.CitaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CitaViewModel @Inject constructor(
    private val repository: CitaRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent = _uiEvent.asSharedFlow()

    var descripcion by mutableStateOf("")
        private set
    var fecha by mutableStateOf("")
        private set
    var hora by mutableStateOf("")
        private set
    var placa by mutableStateOf("")
        private set
    var isLoading by mutableStateOf(false)
        private set
    var isSaving by mutableStateOf(false)
        private set

    var servicios by mutableStateOf<List<Servicio>>(emptyList())
        private set
    var servicioSeleccionadoId by mutableStateOf<Int?>(null)
        private set

    private var establecimientoId: Int? = null
    private var servicioId: Int? = null
    private var establecimientoIdCita: Int = 0
    private var fechaOriginal: String = ""
    private var horaOriginal: String = ""

    private fun setCita(cita: CitaResponse) {
        fecha = cita.fecha
        hora = cita.hora
        placa = cita.vehiculo_placa ?: ""
        descripcion = cita.descripcion ?: ""
        fechaOriginal = cita.fecha
        horaOriginal = cita.hora
        servicioSeleccionadoId = cita.servicio.takeIf { it != 0 }
        establecimientoIdCita = cita.establecimiento
        if (cita.establecimiento != 0) {
            cargarServicios(cita.establecimiento)
        }
    }

    fun cargarCita(id: Int) {
        viewModelScope.launch {
            isLoading = true
            repository.obtenerCitaPorId(id)
                .onSuccess { setCita(it) }
                .onFailure { emitirError("Error al cargar la cita") }
            isLoading = false
        }
    }

    fun cargarServicios(estId: Int) {
        viewModelScope.launch {
            repository.getServicios(estId)
                .onSuccess { servicios = it }
        }
    }

    private fun validar(): String? = when {
        fecha.isBlank() -> "Selecciona una fecha"
        hora.isBlank()  -> "Selecciona una hora"
        placa.isBlank() -> "Ingresa la placa del vehículo"
        else            -> null
    }

    fun crearCita() {
        val error = validar()
        if (error != null) { emitirError(error); return }
        if (establecimientoId == null || servicioId == null) {
            emitirError("Selecciona establecimiento y servicio"); return
        }

        viewModelScope.launch {
            isSaving = true
            val request = CrearCitaRequest(
                establecimiento = establecimientoId!!,
                fecha           = fecha,
                hora            = hora,
                servicio        = servicioId!!,
                placa           = placa.trim().uppercase(),
                descripcion     = descripcion.takeIf { it.isNotBlank() }
            )
            val response = repository.crearCita(request)
            if (response.isSuccessful) {
                mostrarNotificacionLocal(fecha, hora)
                emitirMensaje("Cita creada correctamente")
            } else {
                val body = response.errorBody()?.string()
                emitirError(body ?: "Error al crear cita")
            }
            isSaving = false
        }
    }

    fun actualizarCita(id: Int) {
        val error = validar()
        if (error != null) { emitirError(error); return }

        viewModelScope.launch {
            isSaving = true

            // Pre-verificar horario solo si fecha u hora cambiaron
            val fechaHoraCambiada = fecha != fechaOriginal || hora != horaOriginal
            if (fechaHoraCambiada && establecimientoIdCita != 0) {
                val ocupadas = repository.getCitasOcupadas(establecimientoIdCita, fecha)
                val horaNorm = hora.take(5) // "HH:mm"
                val ocupado = ocupadas.getOrNull()?.any { it.startsWith(horaNorm) } == true
                if (ocupado) {
                    emitirError("Este horario ya está ocupado, por favor elige otro")
                    isSaving = false
                    return@launch
                }
            }

            val request = ActualizarCitaRequest(
                fecha       = fecha,
                hora        = hora,
                placa       = placa.trim().uppercase().takeIf { it.isNotBlank() },
                descripcion = descripcion.takeIf { it.isNotBlank() },
                servicio    = servicioSeleccionadoId
            )
            val response = repository.actualizarCita(id, request)
            if (response.isSuccessful) {
                // Recargar la cita actualizada desde el servidor para sincronizar el estado local
                repository.obtenerCitaPorId(id).onSuccess { setCita(it) }
                emitirMensaje("Cita actualizada correctamente")
            } else {
                val msg = if (response.code() == 500) {
                    "Este horario ya está ocupado, por favor elige otro"
                } else {
                    response.errorBody()?.string() ?: "Error al actualizar"
                }
                emitirError(msg)
            }
            isSaving = false
        }
    }

    fun setIds(establecimiento: Int, servicio: Int) {
        establecimientoId = establecimiento
        servicioId        = servicio
    }

    fun onDescripcionChange(value: String)  { descripcion = value }
    fun onPlacaChange(value: String)        { placa = value }
    fun seleccionarFecha(value: String)     { fecha = value }
    fun onHoraChange(value: String)         { hora = value }
    fun onServicioChange(id: Int)           { servicioSeleccionadoId = id }

    private fun mostrarNotificacionLocal(fecha: String, hora: String) {
        val notification = NotificationCompat.Builder(context, "citas")
            .setSmallIcon(R.drawable.logo_solo)
            .setContentTitle("Cita agendada")
            .setContentText("Tu cita para el $fecha a las $hora fue confirmada")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(System.currentTimeMillis().toInt(), notification)
    }

    private fun emitirError(msg: String) {
        viewModelScope.launch { _uiEvent.emit(UiEvent.ShowError(msg)) }
    }
    private fun emitirMensaje(msg: String) {
        viewModelScope.launch { _uiEvent.emit(UiEvent.ShowMessage(msg)) }
    }
}
