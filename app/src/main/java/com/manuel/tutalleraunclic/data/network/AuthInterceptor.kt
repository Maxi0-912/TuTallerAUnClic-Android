package com.manuel.tutalleraunclic.data.network

import com.manuel.tutalleraunclic.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenManager: TokenManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest = chain.request()

        val token = tokenManager.getToken()

        val requestBuilder = originalRequest.newBuilder()

        // 🔐 Solo agregar token si existe
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        val response = chain.proceed(requestBuilder.build())

        // ⚠️ Manejo básico de expiración
        if (response.code == 401) {
            // Aquí puedes limpiar sesión si quieres
            tokenManager.clearToken()
        }

        return response
    }
}