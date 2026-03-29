package com.manuel.tutalleraunclic.core.navigation



object Routes {





    const val EDITAR_PERFIL = "editar_perfil"

    const val MIS_CITAS = "mis_citas"
    const val MAPA = "mapa"
    const val PERFIL = "perfil"


    const val REGISTER = "register"
    const val LOGIN = "login"
    const val ESTABLECIMIENTOS = "establecimientos"
    const val DETALLE = "detalle"
    const val DETALLE_ARG = "detalle/{id}"



    const val CITA = "cita"
    const val CITA_ARG = "cita/{establecimientoId}/{servicioId}"


    fun cita(establecimientoId: Int, servicioId: Int) =
        "cita/$establecimientoId/$servicioId"

    fun detalle(id: String) = "$DETALLE/$id"
}