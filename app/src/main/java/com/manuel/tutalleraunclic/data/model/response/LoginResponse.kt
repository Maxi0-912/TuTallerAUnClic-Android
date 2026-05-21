package com.manuel.tutalleraunclic.data.model.response

import com.google.gson.annotations.SerializedName
import com.manuel.tutalleraunclic.data.model.entity.Usuario

data class LoginResponse(
    val access: String,
    val refresh: String,
    // Backend may return rol_nombre at the top level or nested inside user/usuario
    @SerializedName("rol_nombre") val rolNombreDirecto: String? = null,
    @SerializedName("user")       val user: Usuario? = null,
    @SerializedName("usuario")    val usuarioObj: Usuario? = null,
) {
    /** Whichever field the backend uses, this resolves to the user object. */
    val resolvedUser: Usuario? get() = user ?: usuarioObj

    /** Safe role name, lowercased, defaults to "cliente" when absent.
     *  Priority: top-level rol_nombre → nested user.rol_nombre → "cliente" */
    val rolNombre: String get() =
        rolNombreDirecto?.lowercase()?.trim()?.takeIf { it.isNotBlank() }
            ?: resolvedUser?.rol_nombre?.lowercase()?.trim()
            ?: "cliente"
}
