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

    fun cargarEstablecimientos(lat: Double, lng: Double) {

        val apiData = listOf(
            Establecimiento(
                id = "1",
                nombre = "Taller El Pro",
                direccion = "Calle 123",
                calificacion = 4.5,
                imagen = "https://picsum.photos/400"
            ),
            Establecimiento(
                id = "2",
                nombre = "Lavadero Clean",
                direccion = "Carrera 45",
                calificacion = 4.2,
                imagen = "https://picsum.photos/401"
            )
        )

        lista.clear()
        lista.addAll(apiData.map { it.toUI() })
    }
}