package com.manuel.tutalleraunclic.data.local

import javax.inject.Inject

class SessionManager @Inject constructor(
    private val tokenManager: TokenManager
) {

    fun getUserId(): Int {
        return tokenManager.getUserId() ?: 0
    }
}