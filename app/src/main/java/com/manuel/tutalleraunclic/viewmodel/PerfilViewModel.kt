package com.manuel.tutalleraunclic.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.request.UpdateUserRequest
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val repository: MainRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<UiEvent>()
    val uiEvent: SharedFlow<UiEvent> = _uiEvent.asSharedFlow()

    // ---------------- CARGAR PERFIL ----------------

    fun cargarPerfil() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.getPerfil()
                .onSuccess { usuario ->
                    _uiState.update { it.copy(isLoading = false, usuario = usuario) }
                }
                .onFailure { e ->
                    val msg = e.message ?: "Error al cargar perfil"
                    _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
                    _uiEvent.emit(UiEvent.ShowError(msg))
                }
        }
    }

    // ---------------- ACTUALIZAR PERFIL (solo texto) ----------------

    fun actualizarPerfil(data: UpdateUserRequest) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.actualizarPerfil(data)
                .onSuccess { usuario ->
                    _uiState.update { it.copy(isLoading = false, usuario = usuario) }
                    _uiEvent.emit(UiEvent.ShowMessage("Perfil actualizado"))
                }
                .onFailure { e ->
                    val msg = e.message ?: "Error al actualizar"
                    _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
                    _uiEvent.emit(UiEvent.ShowError(msg))
                }
        }
    }

    // ---------------- FOTO URI ----------------

    fun setFotoUri(uri: Uri?) {
        _uiState.update { it.copy(fotoUri = uri) }
    }

    // ---------------- ACTUALIZAR PERFIL CON FOTO (multipart) ----------------

    fun actualizarPerfilConFoto(data: UpdateUserRequest, fotoUri: Uri?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val fotoPart = fotoUri?.toMultipartPart()

            // Si la conversión del Uri fue exitosa usamos multipart; si no, JSON plano
            val result = if (fotoPart != null) {
                repository.actualizarPerfilConFoto(data, fotoPart)
            } else {
                repository.actualizarPerfil(data)
            }

            result
                .onSuccess { usuario ->
                    _uiState.update { it.copy(isLoading = false, usuario = usuario) }
                    _uiEvent.emit(UiEvent.ShowMessage("Perfil actualizado"))
                }
                .onFailure { e ->
                    val msg = e.message ?: "Error al actualizar"
                    _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
                    _uiEvent.emit(UiEvent.ShowError(msg))
                }
        }
    }

    // Convierte un Uri de galería a MultipartBody.Part usando ApplicationContext
    private fun Uri.toMultipartPart(): MultipartBody.Part? {
        return try {
            val inputStream = context.contentResolver.openInputStream(this) ?: return null
            val bytes = inputStream.readBytes()
            inputStream.close()

            val mimeType = context.contentResolver.getType(this) ?: "image/jpeg"
            val requestBody = bytes.toRequestBody(mimeType.toMediaTypeOrNull())

            // "foto" debe coincidir con el nombre del campo en el serializer de Django
            MultipartBody.Part.createFormData("foto", "foto.jpg", requestBody)
        } catch (e: Exception) {
            null
        }
    }

    // ---------------- LOGOUT ----------------

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _uiState.update { it.copy(success = true) }
        }
    }

    // ---------------- DESACTIVAR CUENTA ----------------

    fun desactivarCuenta() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            repository.actualizarPerfil(UpdateUserRequest(is_active = false))
                .onSuccess {
                    _uiEvent.emit(UiEvent.ShowMessage("Tu cuenta ha sido desactivada. Contacta soporte para reactivarla."))
                    repository.logout()
                    _uiState.update { it.copy(isLoading = false, success = true) }
                }
                .onFailure { e ->
                    val msg = e.message ?: "Error al desactivar cuenta"
                    _uiState.update { it.copy(isLoading = false, errorMessage = msg) }
                    _uiEvent.emit(UiEvent.ShowError(msg))
                }
        }
    }

    fun resetState() {
        _uiState.value = PerfilUiState()
    }
}
