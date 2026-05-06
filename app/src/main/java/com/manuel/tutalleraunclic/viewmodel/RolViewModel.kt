package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import com.manuel.tutalleraunclic.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class RolViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _rolNombre = MutableStateFlow(tokenManager.getRolNombre())
    val rolNombre: StateFlow<String?> = _rolNombre.asStateFlow()

    fun refresh() {
        _rolNombre.value = tokenManager.getRolNombre()
    }
}
