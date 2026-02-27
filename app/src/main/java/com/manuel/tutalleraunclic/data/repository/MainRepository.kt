package com.manuel.tutalleraunclic.data.repository

import android.content.Context
import com.manuel.tutalleraunclic.data.model.LoginRequest
import com.manuel.tutalleraunclic.data.remote.RetrofitClient

class MainRepository(context: Context) {

    private val api = RetrofitClient.create(context)

    suspend fun login(request: LoginRequest) =
        api.login(request)
}