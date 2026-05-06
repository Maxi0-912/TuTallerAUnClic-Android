package com.manuel.tutalleraunclic.utils

private const val NGROK_BASE = "https://unthinkingly-unsoporiferous-brentley.ngrok-free.dev"

/**
 * Reemplaza la URL local del servidor Django (127.0.0.1 o localhost)
 * por la URL pública de ngrok para que las imágenes sean accesibles desde Android.
 */
fun fixImageUrl(url: String?): String? {
    if (url.isNullOrBlank()) return null
    return url
        .replace("http://127.0.0.1:8000", NGROK_BASE)
        .replace("http://localhost:8000", NGROK_BASE)
}
