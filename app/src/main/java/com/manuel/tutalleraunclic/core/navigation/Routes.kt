package com.manuel.tutalleraunclic.core.navigation

object Routes {

    const val LOGIN = "login"
    const val REGISTER = "register"

    const val ESTABLECIMIENTOS = "establecimientos"
    const val MIS_CITAS = "mis_citas"
    const val MAPA = "mapa"
    const val PERFIL = "perfil"

    const val DETALLE_ESTABLECIMIENTO = "detalle_establecimiento/{id}"
    const val CREAR_CITA = "crear_cita"

    fun detalleEstablecimiento(id: Int) = "detalle_establecimiento/$id"
}