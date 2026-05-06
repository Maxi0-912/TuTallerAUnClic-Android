package com.manuel.tutalleraunclic.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.manuel.tutalleraunclic.core.navigation.Routes

/**
 * Ícono de casa que lleva al inicio (lista de establecimientos) limpiando el backstack.
 * Úsalo en cualquier TopAppBar como `actions = { BotonVolverInicio(navController) }`.
 */
@Composable
fun BotonVolverInicio(navController: NavController) {
    IconButton(
        onClick = {
            navController.navigate(Routes.ESTABLECIMIENTOS) {
                popUpTo(0) { inclusive = true }
                launchSingleTop = true
            }
        }
    ) {
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "Volver al inicio"
        )
    }
}
