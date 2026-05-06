package com.manuel.tutalleraunclic.data.model.entity

import com.manuel.tutalleraunclic.utils.fixImageUrl

val Usuario.displayUsername: String get() = username.orEmpty().ifBlank { "Sin usuario" }
val Usuario.displayNombre: String get() = buildString {
    val n = first_name.orEmpty().trim()
    val a = last_name.orEmpty().trim()
    if (n.isBlank() && a.isBlank()) append("Sin nombre") else append("$n $a".trim())
}
val Usuario.displayEmail: String get() = email.orEmpty().ifBlank { "Sin correo" }
val Usuario.displayTelefono: String get() = telefono.orEmpty().ifBlank { "Sin teléfono" }
val Usuario.displayRol: String get() = rol_nombre.orEmpty().ifBlank { "Sin rol" }
val Usuario.avatarUrl: String get() = fixImageUrl(foto_url)
    ?: "https://i.pravatar.cc/300?u=$id"
