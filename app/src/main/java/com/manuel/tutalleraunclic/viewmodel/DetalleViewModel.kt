package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetalleViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleUiState())
    val uiState: StateFlow<DetalleUiState> = _uiState.asStateFlow()

    fun cargar(id: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Los 3 requests corren en paralelo
            val detalleJob   = async { repository.getDetalleEstablecimiento(id) }
            val serviciosJob = async { runCatching { repository.getServicios(id) } }
            val resenasJob   = async { repository.getResenasEstablecimiento(id) }

            val detalleResult   = detalleJob.await()
            val serviciosResult = serviciosJob.await()
            val resenasResult   = resenasJob.await()

            _uiState.update {
                it.copy(
                    isLoading       = false,
                    establecimiento = detalleResult.getOrNull(),
                    servicios       = serviciosResult.getOrElse { emptyList() },
                    resenas         = resenasResult.getOrElse { emptyList() },
                    error           = detalleResult.exceptionOrNull()?.message
                )
            }
        }
    }
}
