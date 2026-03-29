package com.manuel.tutalleraunclic.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.ui.graphics.Color
import com.manuel.tutalleraunclic.core.navigation.Routes

data class BottomItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

@Composable
fun BottomBar(
    navController: NavController
) {

    val items = listOf(
        BottomItem(Routes.ESTABLECIMIENTOS, Icons.Default.Home, "Inicio"),
        BottomItem("citas", Icons.Default.DateRange, "Citas"),
        BottomItem("perfil", Icons.Default.Person, "Perfil")
    )

    val navBackStackEntry = navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry.value?.destination?.route

    NavigationBar {

        items.forEach { item ->

            val selected = currentRoute == item.route

            val color by animateColorAsState(
                if (selected) MaterialTheme.colorScheme.primary
                else Color.Gray
            )

            NavigationBarItem(
                selected = selected,

                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(Routes.ESTABLECIMIENTOS)
                        launchSingleTop = true
                    }
                },

                icon = {
                    BadgedBox(
                        badge = {
                            // 🔴 Badge solo en citas (ejemplo)
                            if (item.route == "citas") {
                                Badge {
                                    Text("1") // luego lo conectas a datos reales
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = color
                        )
                    }
                },

                label = {
                    Text(
                        text = item.label,
                        color = color
                    )
                }
            )
        }
    }
}