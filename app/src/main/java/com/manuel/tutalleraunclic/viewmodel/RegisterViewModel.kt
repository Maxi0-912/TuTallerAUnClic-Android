package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.request.RegisterRequest
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: MainRepository
) : ViewModel() {

    fun register(
        username: String,
        email: String,
        password: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {

                val request = RegisterRequest(
                    username = username,
                    email = email,
                    password = password
                )

                val response = repository.register(request)

                if (response.isSuccessful) {
                    onResult(true)
                } else {
                    onResult(false)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }
}