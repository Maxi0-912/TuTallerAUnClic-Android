package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.request.LoginRequest
import com.manuel.tutalleraunclic.data.model.response.LoginResponse
import com.manuel.tutalleraunclic.data.repository.MainRepository
import com.manuel.tutalleraunclic.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: MainRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    val loginResult = MutableLiveData<LoginResponse?>()

    fun login(username: String, password: String) {

        viewModelScope.launch {
            try {
                val response = repository.login(LoginRequest(username, password))

                if (response.isSuccessful) {
                    val loginResponse = response.body()

                    loginResponse?.let {
                        tokenManager.saveToken(it.access)
                        loginResult.value = it
                    }

                } else {
                    println("ERROR BACKEND: ${response.code()} - ${response.errorBody()?.string()}")
                }

            } catch (e: Exception) {
                println("ERROR CONEXION: ${e.message}")
            }
        }
    }
}