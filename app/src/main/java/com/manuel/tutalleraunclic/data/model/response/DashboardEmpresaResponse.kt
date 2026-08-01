package com.manuel.tutalleraunclic.data.model.response

data class DashboardEmpresaResponse(
    val resumen_general: ResumenGeneralDashboard,
    val por_establecimiento: List<EstablecimientoDashboard>
)

data class ResumenGeneralDashboard(
    val total_establecimientos: Int,
    val total_citas_mes: Int,
    val pendientes_hoy: Int,
    val calificacion_promedio: Double?
)

data class EstablecimientoDashboard(
    val id: Int,
    val nombre: String,
    val tipo: String,
    val foto_url: String?,
    val calificacion: Double?,
    val total_citas_mes: Int,
    val pendientes_hoy: Int,
    val citas_por_estado: List<CitaPorEstadoDashboard>
)

data class CitaPorEstadoDashboard(
    val estado: String,
    val total: Int
)
