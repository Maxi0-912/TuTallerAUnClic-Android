package com.manuel.tutalleraunclic.core.navigation

import androidx.navigation.NavController

/**
 * Navigates to the correct home screen based on the user's role, clearing
 * [fromRoute] from the back stack so the user cannot go back to it.
 *
 * Current routing:
 *  - "empresa" → EMPRESA_HOME
 *  - "cliente" / null → ESTABLECIMIENTOS
 */
fun NavController.navigateByRole(rolNombre: String?, fromRoute: String) {
    val destination = resolveHomeRoute(rolNombre)
    navigate(destination) {
        popUpTo(fromRoute) { inclusive = true }
        launchSingleTop = true
    }
}

/**
 * Returns the home route for a given role.
 * Centralises the role → route mapping so it is updated in one place.
 */
fun resolveHomeRoute(rolNombre: String?): String =
    when (rolNombre?.lowercase()?.trim()) {
        "empresa" -> Routes.EMPRESA_HOME
        else      -> Routes.ESTABLECIMIENTOS
    }
