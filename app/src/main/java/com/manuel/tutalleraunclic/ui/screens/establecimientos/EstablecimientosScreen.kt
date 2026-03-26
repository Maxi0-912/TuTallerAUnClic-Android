package com.manuel.tutalleraunclic.ui.screens.establecimientos

import androidx.compose.runtime.*
import androidx.compose.foundation.lazy.*
import androidx.navigation.NavController
import androidx.compose.runtime.LaunchedEffect
import androidx.hilt.navigation.compose.hiltViewModel
import  com.manuel.tutalleraunclic.core.navigation.Routes
import com.manuel.tutalleraunclic.viewmodel.EstablecimientoViewModel
import com.manuel.tutalleraunclic.ui.components.EstablecimientoCard

@Composable
fun EstablecimientosScreen(
    navController: NavController,
    viewModel: EstablecimientoViewModel = hiltViewModel()
) {

    val lista = viewModel.lista

    LaunchedEffect(Unit) {
        viewModel.cargarEstablecimientos(3.4516, -76.5320)
    }

    LazyColumn {

        items(lista) { item ->

            EstablecimientoCard(
                establecimiento = item,

                // 👇 Click en la card (detalle)
                onClick = {
                    navController.navigate("detalle_establecimiento/${item.id}")
                },

                // 🔥 BOTÓN AGENDAR
                onAgendarClick = {
                    navController.navigate("${Routes.CITA}/${item.id}")
                }

            )
        }
    }
}