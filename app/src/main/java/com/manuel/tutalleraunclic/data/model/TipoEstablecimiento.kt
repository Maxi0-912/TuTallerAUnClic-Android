package com.manuel.tutalleraunclic.data.model

sealed class TipoEstablecimiento {
    object Taller   : TipoEstablecimiento()
    object Lavadero : TipoEstablecimiento()
    data class Otro(val nombre: String) : TipoEstablecimiento()

    companion object {
        fun from(nombre: String?): TipoEstablecimiento =
            when (nombre?.lowercase()?.trim()) {
                "taller"   -> Taller
                "lavadero" -> Lavadero
                else       -> Otro(nombre.orEmpty())
            }
    }
}
