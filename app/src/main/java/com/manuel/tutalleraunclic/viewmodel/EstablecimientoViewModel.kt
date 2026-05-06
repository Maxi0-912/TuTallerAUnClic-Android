package com.manuel.tutalleraunclic.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateListOf
import com.manuel.tutalleraunclic.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

import com.manuel.tutalleraunclic.data.model.EstablecimientoUI
import com.manuel.tutalleraunclic.data.model.toUI
import com.manuel.tutalleraunclic.data.repository.MainRepository

@HiltViewModel
class EstablecimientoViewModel @Inject constructor(
    private val repository: MainRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var lista = mutableStateListOf<EstablecimientoUI>()
    var isLoading by mutableStateOf(false)
    var error by mutableStateOf<String?>(null)

    private var userLat: Double? = null
    private var userLng: Double? = null

    /**
     * One-shot messages to show in the home screen (e.g. "Modo empresa no disponible").
     * Emitted at most once per login session; cleared from storage on first consumption.
     */
    private val _pendingMessage = MutableSharedFlow<String>(replay = 0)
    val pendingMessage: SharedFlow<String> = _pendingMessage.asSharedFlow()

    init {
        cargarEstablecimientos()
    }

    /** Call once when the home screen is visible. Emits any stored one-shot message. */
    fun checkAndEmitPendingMessage() {
        viewModelScope.launch {
            val msg = tokenManager.getPendingMessage() ?: return@launch
            tokenManager.clearPendingMessage()
            _pendingMessage.emit(msg)
        }
    }

    fun actualizarUbicacion(lat: Double, lng: Double) {
        userLat = lat
        userLng = lng
        val updated = lista
            .map { ui -> ui.copy(distanciaKm = calcularDistancia(lat, lng, ui.latitud, ui.longitud)) }
            .sortedBy { it.distanciaKm }
        lista.clear()
        lista.addAll(updated)
    }

    fun cargarEstablecimientos() {
        viewModelScope.launch {
            isLoading = true
            error = null
            repository.getEstablecimientos()
                .onSuccess { establecimientos ->
                    val mapped = establecimientos
                        .map { it.toUI(userLat, userLng) }
                        .let { if (userLat != null) it.sortedBy { e -> e.distanciaKm } else it }
                    lista.clear()
                    lista.addAll(mapped)
                    isLoading = false
                }
                .onFailure { e ->
                    error = e.message
                    isLoading = false
                }
        }
    }

    private fun calcularDistancia(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2).pow(2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2).pow(2)
        return r * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    }

    private fun Double.pow(n: Int): Double = Math.pow(this, n.toDouble())
}
