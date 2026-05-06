package com.manuel.tutalleraunclic.core.navigation

object Routes {

    const val LOGIN = "login"
    const val REGISTER = "register"

    const val ESTABLECIMIENTOS = "establecimientos"
    const val DETALLE     = "detalle"
    const val DETALLE_ARG = "detalle/{id}"

    // Rutas específicas por tipo de establecimiento
    const val TALLER      = "taller"
    const val TALLER_ARG  = "taller/{id}"
    const val LAVADERO     = "lavadero"
    const val LAVADERO_ARG = "lavadero/{id}"

    const val CITA     = "cita"
    const val CITA_ARG = "cita/{establecimientoId}/{servicioId}"

    const val MIS_CITAS    = "mis_citas"
    const val CREAR_CITA   = "crear_cita"
    const val EDITAR_CITA  = "editar_cita"
    const val EDITAR_CITA_ARG = "editar_cita/{citaId}"

    const val MAPA = "mapa"

    const val PERFIL       = "perfil"
    const val EDITAR_PERFIL = "editar_perfil"

    const val NOTIFICACIONES = "notificaciones"

    const val EMPRESA_HOME            = "empresa_home"
    const val EMPRESA_DASHBOARD       = "empresa_dashboard"
    const val EMPRESA_CITAS           = "empresa_citas"
    const val EMPRESA_ESTABLECIMIENTO = "empresa_establecimiento"

    const val RESENA_ARG = "resena/{citaId}/{establecimientoId}"

    fun cita(establecimientoId: Int, servicioId: Int) =
        "cita/$establecimientoId/$servicioId"

    fun detalle(id: String)  = "$DETALLE/$id"
    fun taller(id: Int)      = "$TALLER/$id"
    fun lavadero(id: Int)    = "$LAVADERO/$id"

    fun resena(citaId: Int, establecimientoId: Int) =
        "resena/$citaId/$establecimientoId"
}
