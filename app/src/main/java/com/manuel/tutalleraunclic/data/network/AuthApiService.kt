package com.manuel.tutalleraunclic.data.network

import com.manuel.tutalleraunclic.data.model.request.LoginRequest
import com.manuel.tutalleraunclic.data.model.response.LoginResponse
import com.manuel.tutalleraunclic.data.model.response.RefreshResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {

    @POST("usuarios/login/")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    // NO suspend — requerido por Authenticator sincrónico
    // Si el refresh falla, verificar la ruta correcta en el main urls.py del backend
    @POST("usuarios/login/refresh/")
    fun refreshToken(
        @Body body: Map<String, String>
    ): Call<RefreshResponse>
}