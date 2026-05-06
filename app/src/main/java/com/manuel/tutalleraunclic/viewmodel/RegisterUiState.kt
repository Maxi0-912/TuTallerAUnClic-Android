package com.manuel.tutalleraunclic.viewmodel

data class RegisterUiState(
    val isLoading: Boolean = false,
    val success: Boolean = false,
    val rolRegistrado: String? = null,
    val errorMessage: String? = null
)