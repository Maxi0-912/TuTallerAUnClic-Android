package com.manuel.tutalleraunclic.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.manuel.tutalleraunclic.data.model.response.CitaResponse

@Composable
fun CitaItem(
    cita: CitaResponse,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onCalificar: (() -> Unit)? = null
) {
    val estadoColor = when (cita.estado.lowercase()) {
        "confirmada" -> Color(0xFF2E7D32)
        "pendiente"  -> Color(0xFFF57C00)
        "finalizada" -> Color(0xFF1565C0)
        "cancelada"  -> Color(0xFFC62828)
        else         -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cita #${cita.id}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Surface(
                    color = estadoColor.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = cita.estado.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = estadoColor
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            cita.establecimiento_nombre?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.SemiBold)
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            Text(
                text = "📅 ${cita.fecha}  •  ⏰ ${cita.hora}",
                style = MaterialTheme.typography.bodyMedium
            )

            cita.vehiculo_placa?.let {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "🚗 $it",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            val servicio = cita.servicio_nombre ?: cita.servicio_texto
            if (!servicio.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "🔧 $servicio",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            when {
                cita.estado.lowercase() == "finalizada" && cita.tiene_resena -> {
                    Text(
                        "Ya calificaste este servicio",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                cita.estado.lowercase() == "finalizada" -> {
                    Button(
                        onClick = { onCalificar?.invoke() },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Icon(Icons.Default.Star, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Calificar servicio")
                    }
                }
                cita.estado.lowercase() == "cancelada" -> { /* sin acciones */ }
                else -> {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedButton(onClick = onEdit, modifier = Modifier.weight(1f)) {
                            Text("Editar")
                        }
                        Button(
                            onClick = onDelete,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Text("Cancelar")
                        }
                    }
                }
            }
        }
    }
}
