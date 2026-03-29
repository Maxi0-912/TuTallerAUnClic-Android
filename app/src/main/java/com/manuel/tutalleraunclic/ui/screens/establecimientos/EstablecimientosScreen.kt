package com.manuel.tutalleraunclic.ui.screens.establecimientos

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.manuel.tutalleraunclic.core.navigation.Routes
import com.manuel.tutalleraunclic.data.model.EstablecimientoUI
import com.manuel.tutalleraunclic.ui.components.EstablecimientoCardPro
import com.manuel.tutalleraunclic.ui.components.BottomBar


// EstablecimientosScreen.kt
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EstablecimientosScreen(
    navController: NavController
) {

    val lista = listOf(
        EstablecimientoUI(1, "Taller El Rápido", "", 4.5, 120, "Centro", "$50.000"),
        EstablecimientoUI(2, "AutoFix Pro", "", 4.8, 90, "Envigado", "$70.000"),
        EstablecimientoUI(3, "CleanCar", "", 4.2, 60, "Bello", "$40.000")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Establecimientos") }
            )
        },
        bottomBar = {
            BottomBar(navController)
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            items(lista) { item ->

                EstablecimientoCardPro(
                    establecimiento = item,
                    onClick = {
                        navController.navigate(
                            Routes.detalle(item.id.toString())
                        )
                    },
                    onAgendarClick = {
                        navController.navigate(
                            Routes.cita(item.id, 1)
                        )
                    }
                )
            }
        }
    }
}