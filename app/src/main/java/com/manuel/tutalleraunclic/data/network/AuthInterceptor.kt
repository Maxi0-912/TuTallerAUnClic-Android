package com.manuel.tutalleraunclic.data.network

import com.manuel.tutalleraunclic.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response
import android.content.Context

class AuthInterceptor(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        val tokenManager = TokenManager(context)
        val token = tokenManager.getToken()

        val request = chain.request().newBuilder()

        token?.let {
            request.addHeader("Authorization", "Bearer $it")
        }

        return chain.proceed(request.build())
    }
}