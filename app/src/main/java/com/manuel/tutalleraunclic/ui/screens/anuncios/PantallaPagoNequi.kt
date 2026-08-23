package com.manuel.tutalleraunclic.ui.screens.anuncios

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

private const val NEQUI_NUMERO = "3167599778"
private const val NEQUI_MONTO = 30000
private const val NEQUI_NEGOCIO = "Manuel Mazabuel"

/**
 * Pantalla informativa de pago Nequi: solo muestra los datos para transferir
 * a mano y una referencia. No hay endpoint de verificación de pago acordado
 * todavía, así que "Ya transferí" solo cierra la pantalla — la confirmación
 * del pago se sigue coordinando manualmente, como ya indica el mensaje que
 * muestra AnuncioFormViewModel al crear un anuncio que requiere pago.
 */
@Composable
fun PantallaPagoNequi(anuncioId: Int, onCerrar: () -> Unit) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    val referencia = "TT%06d".format(anuncioId)

    fun copiar(valor: String) {
        clipboard.setText(AnnotatedString(valor))
        Toast.makeText(context, "Copiado", Toast.LENGTH_SHORT).show()
    }

    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(Modifier.height(24.dp))
                Text("💳", fontSize = 48.sp)
                Spacer(Modifier.height(8.dp))
                Text(
                    "Transferencia Nequi",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(24.dp))

                Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        DatoPago(label = "Destinatario", valor = NEQUI_NEGOCIO)
                        DatoPago(label = "Número Nequi", valor = NEQUI_NUMERO, onCopiar = { copiar(NEQUI_NUMERO) })
                        DatoPago(label = "Monto", valor = "$${"%,d".format(NEQUI_MONTO)} COP")
                        DatoPago(label = "Referencia", valor = referencia, onCopiar = { copiar(referencia) })
                    }
                }

                Spacer(Modifier.height(16.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Pasos", fontWeight = FontWeight.Bold)
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "1. Abre tu app Nequi\n" +
                                "2. Envía $${"%,d".format(NEQUI_MONTO)} al $NEQUI_NUMERO\n" +
                                "3. En el mensaje de la transferencia escribe: $referencia\n" +
                                "4. Confirma la transferencia",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = onCerrar,
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Ya transferí")
                }

                Spacer(Modifier.height(8.dp))
                Text(
                    "Verificaremos tu pago y activaremos el anuncio manualmente.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun DatoPago(label: String, valor: String, onCopiar: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .let { if (onCopiar != null) it.clickable(onClick = onCopiar) else it },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(valor, fontWeight = FontWeight.Bold)
            if (onCopiar != null) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Copiar", modifier = Modifier.size(16.dp))
            }
        }
    }
}
