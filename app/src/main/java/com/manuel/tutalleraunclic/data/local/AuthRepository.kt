package com.manuel.tutalleraunclic.data.local

import java.net.HttpURLConnection
import java.net.URL

fun refreshToken(tokenManager: TokenManager): Boolean {

    val refresh = tokenManager.getRefreshToken() ?: return false

    val url = URL("http://tu_api/auth/refresh/")
    val conn = url.openConnection() as HttpURLConnection

    conn.requestMethod = "POST"
    conn.doOutput = true
    conn.setRequestProperty("Content-Type", "application/json")

    val body = """{"refresh":"$refresh"}"""
    conn.outputStream.write(body.toByteArray())

    return if (conn.responseCode == 200) {

        val response = conn.inputStream.bufferedReader().readText()

        // ⚠️ aquí parseas el nuevo access
        val newAccess = "TOKEN_NUEVO"

        tokenManager.saveTokens(newAccess, refresh)

        true
    } else {
        false
    }
}