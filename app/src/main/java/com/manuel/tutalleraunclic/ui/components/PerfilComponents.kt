package com.manuel.tutalleraunclic.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.manuel.tutalleraunclic.ui.theme.LocalIsDarkMode
import com.manuel.tutalleraunclic.ui.theme.LocalToggleTheme

@Composable
fun TemaCard() {
    val isDark = LocalIsDarkMode.current
    val toggleTheme = LocalToggleTheme.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = if (isDark) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Column {
                    Text(
                        text = if (isDark) "Modo oscuro" else "Modo claro",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                    )
                    Text(
                        text = "Cambiar apariencia",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }
            Switch(checked = isDark, onCheckedChange = { toggleTheme() })
        }
    }
}

@Composable
fun DesactivarCuentaSection(onDesactivar: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    TextButton(
        onClick = { showDialog = true },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Desactivar cuenta", color = MaterialTheme.colorScheme.error)
    }

    if (showDialog) {
        AppAlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Desactivar cuenta") },
            text = { Text("Tu cuenta será desactivada. Podrás reactivarla contactando a soporte. ¿Deseas continuar?") },
            confirmButton = {
                Button(
                    onClick = { showDialog = false; onDesactivar() },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text("Sí, desactivar") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancelar") }
            }
        )
    }
}
