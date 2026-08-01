package com.manuel.tutalleraunclic.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.entity.TipoEstablecimiento
import com.manuel.tutalleraunclic.data.repository.EstablecimientoEmpresaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

data class EstablecimientoFormUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val esEdicion: Boolean = false,
    val establecimientoId: Int? = null,

    val tipos: List<TipoEstablecimiento> = emptyList(),
    val tipoId: Int? = null,

    val nombre: String = "",
    val direccion: String = "",
    val telefono: String = "",
    val descripcion: String = "",
    val horaApertura: String = "",
    val horaCierre: String = "",
    val latitud: String = "",
    val longitud: String = "",

    val fotoUri: Uri? = null,
    val fotoUrlActual: String? = null,

    val guardadoExitoso: Boolean = false
)

@HiltViewModel
class EstablecimientoFormViewModel @Inject constructor(
    private val repository: EstablecimientoEmpresaRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(EstablecimientoFormUiState())
    val uiState: StateFlow<EstablecimientoFormUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    private var yaInicializado = false

    fun inicializar(establecimientoId: Int?) {
        if (yaInicializado) return
        yaInicializado = true
        _uiState.update { it.copy(esEdicion = establecimientoId != null, establecimientoId = establecimientoId) }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            val tipos = repository.getTipos().getOrDefault(emptyList())
            _uiState.update { it.copy(tipos = tipos) }

            if (establecimientoId != null) {
                val establecimiento = repository.getMisEstablecimientos().getOrNull()
                    ?.firstOrNull { it.id == establecimientoId }

                if (establecimiento != null) {
                    _uiState.update {
                        it.copy(
                            nombre = establecimiento.nombre,
                            direccion = establecimiento.direccion ?: "",
                            telefono = establecimiento.telefono ?: "",
                            descripcion = establecimiento.descripcion ?: "",
                            horaApertura = establecimiento.hora_apertura ?: "",
                            horaCierre = establecimiento.hora_cierre ?: "",
                            latitud = establecimiento.latitud ?: "",
                            longitud = establecimiento.longitud ?: "",
                            tipoId = establecimiento.tipo,
                            fotoUrlActual = establecimiento.foto_url
                        )
                    }
                } else {
                    _uiEvent.emit(UiEvent.ShowError("No se pudo cargar el establecimiento"))
                }
            }

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun setNombre(v: String) = _uiState.update { it.copy(nombre = v) }
    fun setDireccion(v: String) = _uiState.update { it.copy(direccion = v) }
    fun setTelefono(v: String) = _uiState.update { it.copy(telefono = v) }
    fun setDescripcion(v: String) = _uiState.update { it.copy(descripcion = v) }
    fun setHoraApertura(v: String) = _uiState.update { it.copy(horaApertura = v) }
    fun setHoraCierre(v: String) = _uiState.update { it.copy(horaCierre = v) }
    fun setLatitud(v: String) = _uiState.update { it.copy(latitud = v) }
    fun setLongitud(v: String) = _uiState.update { it.copy(longitud = v) }
    fun setTipoId(id: Int) = _uiState.update { it.copy(tipoId = id) }
    fun setFotoUri(uri: Uri?) = _uiState.update { it.copy(fotoUri = uri) }

    fun guardar() {
        val state = _uiState.value

        if (state.nombre.isBlank()) {
            viewModelScope.launch { _uiEvent.emit(UiEvent.ShowError("El nombre es obligatorio")) }
            return
        }
        if (state.tipoId == null) {
            viewModelScope.launch { _uiEvent.emit(UiEvent.ShowError("Selecciona un tipo")) }
            return
        }
        if (state.latitud.isBlank() || state.longitud.isBlank()) {
            viewModelScope.launch { _uiEvent.emit(UiEvent.ShowError("La ubicación (latitud y longitud) es obligatoria")) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }

            val fotoPart = state.fotoUri?.toMultipartPart()

            val result = if (state.esEdicion) {
                repository.editar(
                    id = state.establecimientoId!!,
                    nombre = state.nombre,
                    direccion = state.direccion.ifBlank { null },
                    telefono = state.telefono.ifBlank { null },
                    descripcion = state.descripcion.ifBlank { null },
                    horaApertura = state.horaApertura.ifBlank { null },
                    horaCierre = state.horaCierre.ifBlank { null },
                    latitud = state.latitud,
                    longitud = state.longitud,
                    tipoId = state.tipoId,
                    foto = fotoPart
                )
            } else {
                repository.crear(
                    nombre = state.nombre,
                    direccion = state.direccion.ifBlank { null },
                    telefono = state.telefono.ifBlank { null },
                    descripcion = state.descripcion.ifBlank { null },
                    horaApertura = state.horaApertura.ifBlank { null },
                    horaCierre = state.horaCierre.ifBlank { null },
                    latitud = state.latitud,
                    longitud = state.longitud,
                    tipoId = state.tipoId,
                    foto = fotoPart
                )
            }

            result
                .onSuccess {
                    _uiState.update { it.copy(isSaving = false, guardadoExitoso = true) }
                    _uiEvent.emit(UiEvent.ShowMessage(if (state.esEdicion) "Cambios guardados" else "Establecimiento creado"))
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSaving = false) }
                    _uiEvent.emit(UiEvent.ShowError(e.message ?: "No se pudo guardar el establecimiento"))
                }
        }
    }

    private fun Uri.toMultipartPart(): MultipartBody.Part? {
        return try {
            val inputStream = context.contentResolver.openInputStream(this) ?: return null
            val bytes = inputStream.readBytes()
            inputStream.close()

            val mimeType = context.contentResolver.getType(this) ?: "image/jpeg"
            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())

            MultipartBody.Part.createFormData("foto", "foto.jpg", requestBody)
        } catch (e: Exception) {
            null
        }
    }
}
