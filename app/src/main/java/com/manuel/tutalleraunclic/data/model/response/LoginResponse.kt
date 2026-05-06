package com.manuel.tutalleraunclic.data.model.response

import com.google.gson.annotations.SerializedName
import com.manuel.tutalleraunclic.data.model.entity.Usuario

data class LoginResponse(
    val access: String,
    val refresh: String,
    // Backend may return the user object under "user" or "usuario"
    @SerializedName("user")    val user: Usuario? = null,
    @SerializedName("usuario") val usuarioObj: Usuario? = null,
) {
    /** Whichever field the backend uses, this resolves to the user object. */
    val resolvedUser: Usuario? get() = user ?: usuarioObj

    /** Safe role name, lowercased, defaults to "cliente" when absent. */
    val rolNombre: String get() = resolvedUser?.rol_nombre?.lowercase()?.trim() ?: "cliente"
}
