package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.response.DashboardEmpresaResponse
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardEmpresaUiState(
    val isLoading: Boolean = false,
    val data: DashboardEmpresaResponse? = null,
    val error: String? = null
)

@HiltViewModel
class DashboardEmpresaViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardEmpresaUiState())
    val uiState: StateFlow<DashboardEmpresaUiState> = _uiState.asStateFlow()

    init {
        cargarDashboard()
    }

    fun cargarDashboard() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getDashboardEmpresa()
                .onSuccess { data -> _uiState.update { it.copy(isLoading = false, data = data) } }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error al cargar el dashboard") }
                }
        }
    }
}
