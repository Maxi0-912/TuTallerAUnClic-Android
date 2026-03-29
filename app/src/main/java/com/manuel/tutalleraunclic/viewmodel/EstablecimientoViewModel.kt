package com.manuel.tutalleraunclic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateListOf
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

import com.manuel.tutalleraunclic.data.model.entity.Establecimiento
import com.manuel.tutalleraunclic.data.model.toUI
import com.manuel.tutalleraunclic.data.model.EstablecimientoUI

@HiltViewModel
class EstablecimientoViewModel @Inject constructor() : ViewModel() {

    var lista = mutableStateListOf<EstablecimientoUI>()

    fun cargarEstablecimientos() {

        // 🔥 SIMULANDO BACKEND
        val fakeApi = listOf(
            Establecimiento(
                id = 1,
                nombre = "Taller El Pro",
                direccion = "Calle 123",
                telefono = "3001234567",
                hora_apertura = "08:00",
                hora_cierre = "18:00",
                descripcion = "Mecánica general",
                latitud = "6.2442",
                longitud = "-75.5812",
                propietario = 1,
                tipo = 1,
                calificacion = 4.5
            ),
            Establecimiento(
                id = 2,
                nombre = "Lavadero Clean",
                direccion = "Carrera 45",
                telefono = "3007654321",
                hora_apertura = "07:00",
                hora_cierre = "17:00",
                descripcion = "Lavado profesional",
                latitud = "6.2450",
                longitud = "-75.5800",
                propietario = 2,
                tipo = 2,
                calificacion = 4.2
            )
        )

        lista.clear()
        lista.addAll(fakeApi.map { it.toUI() })
    }
}