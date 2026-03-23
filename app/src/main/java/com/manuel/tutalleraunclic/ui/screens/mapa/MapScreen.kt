package com.manuel.tutalleraunclic.ui.screens.mapa

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun MapScreen() {

    val medellin = LatLng(6.2442, -75.5812)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(medellin, 12f)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        GoogleMap(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),   // 👈 tamaño del mapa
            cameraPositionState = cameraPositionState
        ) {

            Marker(
                state = MarkerState(position = medellin),
                title = "Taller Automotriz",
                snippet = "Servicio de mecánica"
            )

        }

    }

}