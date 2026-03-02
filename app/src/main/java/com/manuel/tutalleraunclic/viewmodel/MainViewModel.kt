package com.manuel.tutalleraunclic.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.tutalleraunclic.data.model.LoginRequest
import com.manuel.tutalleraunclic.data.remote.RetrofitClient
import com.manuel.tutalleraunclic.data.repository.MainRepository
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MainRepository(RetrofitClient.api)

    fun login(
        username: String,
        password: String,
        onResult: (Boolean, String?) -> Unit
    ) {

        viewModelScope.launch {

            try {

                val response = repository.login(
                    LoginRequest(username, password)
                )

                if (response.isSuccessful) {
                    val token = response.body()?.access
                    onResult(true, token)
                } else {
                    onResult(false, null)
                }

            } catch (e: Exception) {
                onResult(false, null)
            }
        }
    }
}