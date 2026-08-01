package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.entity.Anuncio
import com.manuel.tutalleraunclic.data.repository.AnuncioRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MisAnunciosUiState(
    val isLoading: Boolean = false,
    val anuncios: List<Anuncio> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class MisAnunciosViewModel @Inject constructor(
    private val repository: AnuncioRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MisAnunciosUiState())
    val uiState: StateFlow<MisAnunciosUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    fun cargarMisAnuncios() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            repository.getMisAnuncios()
                .onSuccess { lista -> _uiState.update { it.copy(isLoading = false, anuncios = lista) } }
                .onFailure { e ->
                    val msg = e.message ?: "Error al cargar tus anuncios"
                    _uiState.update { it.copy(isLoading = false, error = msg) }
                }
        }
    }

    fun eliminarAnuncio(id: Int) {
        viewModelScope.launch {
            repository.eliminarAnuncio(id)
                .onSuccess {
                    _uiState.update { state -> state.copy(anuncios = state.anuncios.filterNot { it.id == id }) }
                    _uiEvent.emit(UiEvent.ShowMessage("Anuncio eliminado"))
                }
                .onFailure { e ->
                    _uiEvent.emit(UiEvent.ShowError(e.message ?: "No se pudo eliminar el anuncio"))
                }
        }
    }
}
