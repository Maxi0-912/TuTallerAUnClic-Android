package com.manuel.tutalleraunclic.data.local

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext context: Context
) {

    private val prefs = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCESS      = "access_token"
        private const val KEY_REFRESH     = "refresh_token"
        private const val KEY_ROL         = "rol_nombre"
        private const val KEY_PENDING_MSG = "pending_message"
    }

    // ── Tokens ──────────────────────────────────────────────────────────────

    fun saveTokens(access: String, refresh: String) {
        prefs.edit()
            .putString(KEY_ACCESS, access)
            .putString(KEY_REFRESH, refresh)
            .apply()
    }

    fun getAccessToken(): String?  = prefs.getString(KEY_ACCESS, null)
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH, null)

    fun hasValidSession(): Boolean =
        !getAccessToken().isNullOrEmpty() && !getRefreshToken().isNullOrEmpty()

    // ── Rol del usuario ──────────────────────────────────────────────────────

    fun saveRolNombre(rolNombre: String) {
        prefs.edit().putString(KEY_ROL, rolNombre.lowercase().trim()).apply()
    }

    /** Returns the saved role ("cliente", "empresa"), or null if not stored. */
    fun getRolNombre(): String? = prefs.getString(KEY_ROL, null)

    // ── Mensaje pendiente (e.g. "modo empresa no disponible") ─────────────────

    fun savePendingMessage(message: String) {
        prefs.edit().putString(KEY_PENDING_MSG, message).apply()
    }

    fun getPendingMessage(): String? = prefs.getString(KEY_PENDING_MSG, null)

    fun clearPendingMessage() {
        prefs.edit().remove(KEY_PENDING_MSG).apply()
    }

    // ── Limpieza total (logout) ───────────────────────────────────────────────

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
