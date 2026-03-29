package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.entity.Cita
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class CitasState {
    object Loading : CitasState()
    data class Success(val citas: List<Cita>) : CitasState()
    data class Error(val message: String) : CitasState()
}

@HiltViewModel
class CitaListViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    private val _state = MutableStateFlow<CitasState>(CitasState.Loading)
    val state: StateFlow<CitasState> = _state

    fun obtenerCitas() {
        viewModelScope.launch {
            _state.value = CitasState.Loading

            try {
                val citas = repository.getCitas()
                _state.value = CitasState.Success(citas)
            } catch (e: Exception) {
                _state.value = CitasState.Error("Error al cargar citas")
            }
        }
    }
}