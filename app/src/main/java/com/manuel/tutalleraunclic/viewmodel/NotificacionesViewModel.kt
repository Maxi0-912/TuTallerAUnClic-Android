package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.local.TokenManager
import com.manuel.tutalleraunclic.data.model.entity.Notificacion
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificacionesViewModel @Inject constructor(
    private val repository: MainRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _notificaciones = MutableStateFlow<List<Notificacion>>(emptyList())
    val notificaciones: StateFlow<List<Notificacion>> = _notificaciones.asStateFlow()

    val unreadCount: StateFlow<Int> = _notificaciones
        .map { list -> list.count { !it.leida } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 0)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        cargarNotificaciones()
        iniciarPolling()
    }

    private fun estaAutenticado() = !tokenManager.getAccessToken().isNullOrEmpty()

    private fun iniciarPolling() {
        viewModelScope.launch {
            while (true) {
                delay(30_000)
                if (estaAutenticado()) {
                    repository.misNotificaciones()
                        .onSuccess { list -> _notificaciones.value = list }
                }
            }
        }
    }

    fun cargarNotificaciones() {
        if (!estaAutenticado()) return
        viewModelScope.launch {
            _isLoading.value = true
            repository.misNotificaciones()
                .onSuccess { list ->
                    _notificaciones.value = list
                    _isLoading.value = false
                }
                .onFailure {
                    _isLoading.value = false
                }
        }
    }

    fun marcarLeida(id: Int) {
        viewModelScope.launch {
            repository.marcarNotificacionLeida(id)
                .onSuccess {
                    _notificaciones.update { list ->
                        list.map { n -> if (n.id == id) n.copy(leida = true) else n }
                    }
                }
        }
    }

    fun marcarTodasLeidas() {
        viewModelScope.launch {
            _notificaciones.value.filter { !it.leida }.forEach { n ->
                repository.marcarNotificacionLeida(n.id)
            }
            _notificaciones.update { list -> list.map { it.copy(leida = true) } }
        }
    }
}
