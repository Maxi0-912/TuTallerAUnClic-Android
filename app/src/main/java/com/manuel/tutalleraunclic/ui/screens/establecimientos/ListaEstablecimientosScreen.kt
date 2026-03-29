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
import com.manuel.tutalleraunclic.ui.components.EstablecimientoCardPro
import com.manuel.tutalleraunclic.data.model.EstablecimientoUI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaEstablecimientosScreen(
    navController: NavController
) {

    // 🔥 DATA DE PRUEBA (para que se vea brutal)
    val listaEstablecimientos = listOf(
        EstablecimientoUI(
            id = 1,
            nombre = "Taller Premium",
            direccion = "Calle 10 #20-30",
            rating = 4.5,
            totalReviews = 120,
            precioDesde = "$30.000",
            imagenUrl = "https://picsum.photos/400/200"
        ),
        EstablecimientoUI(
            id = 2,
            nombre = "Lavadero CleanCar",
            direccion = "Av 5 #12-40",
            rating = 4.2,
            totalReviews = 80,
            precioDesde = "$20.000",
            imagenUrl = "https://picsum.photos/401/200"
        ),
        EstablecimientoUI(
            id = 3,
            nombre = "AutoSpa Pro",
            direccion = "Cra 8 #15-22",
            rating = 4.8,
            totalReviews = 200,
            precioDesde = "$40.000",
            imagenUrl = "https://picsum.photos/402/200"
        )
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Establecimientos") }
            )
        }
    ) { padding ->

        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            items(listaEstablecimientos) { item ->

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