package com.manuel.tutalleraunclic.data.repository

import com.manuel.tutalleraunclic.data.model.response.LoginResponse
import com.manuel.tutalleraunclic.network.AuthApiService
import com.manuel.tutalleraunclic.data.model.request.LoginRequest
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthApiService
) {

    suspend fun login(request: LoginRequest) =
        api.login(request)
}