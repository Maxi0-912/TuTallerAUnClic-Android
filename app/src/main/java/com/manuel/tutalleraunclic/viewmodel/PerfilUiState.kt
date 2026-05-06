package com.manuel.tutalleraunclic.viewmodel

import android.net.Uri
import com.manuel.tutalleraunclic.data.model.entity.Usuario

data class PerfilUiState(
    val usuario: Usuario? = null,
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val errorMessage: String? = null,
    val fotoUri: Uri? = null
)
