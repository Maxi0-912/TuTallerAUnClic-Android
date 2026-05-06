package com.manuel.tutalleraunclic.data.network

import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import com.manuel.tutalleraunclic.data.local.TokenManager

class AuthInterceptor @Inject constructor(
    private val tokenManager: TokenManager
) : Interceptor {

    private val publicPaths = listOf("usuarios/login/", "usuarios/register/")

    override fun intercept(chain: Interceptor.Chain): Response {
        val url = chain.request().url.encodedPath
        val isPublic = publicPaths.any { url.contains(it) }

        val token = tokenManager.getAccessToken()

        val requestBuilder = chain.request().newBuilder()

        if (!isPublic && !token.isNullOrEmpty()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}