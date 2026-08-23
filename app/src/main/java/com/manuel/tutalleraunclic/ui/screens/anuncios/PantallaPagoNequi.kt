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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.manuel.tutalleraunclic.data.model.response.PagoPendienteResponse
import com.manuel.tutalleraunclic.viewmodel.PagoNequiUiState
import com.manuel.tutalleraunclic.viewmodel.PagoNequiViewModel

/**
 * Pantalla de pago Nequi: al abrirse, llama a POST /api/empresa/anuncios/{id}/pago/
 * (crea el PagoPendiente en el backend) y muestra referencia, monto, número
 * Nequi y titular tal como vienen en esa respuesta — nada hardcodeado. Si la
 * llamada falla, se muestra el error en pantalla en vez de datos por defecto.
 */
@Composable
fun PantallaPagoNequi(
    anuncioId: Int,
    onCerrar: () -> Unit,
    viewModel: PagoNequiViewModel = hiltViewModel(key = "pago_nequi_$anuncioId")
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(anuncioId) { viewModel.cargar(anuncioId) }

    Dialog(
        onDismissRequest = onCerrar,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            when (val estado = uiState) {
                is PagoNequiUiState.Cargando -> EstadoCargando()
                is PagoNequiUiState.Error -> EstadoError(
                    mensaje = estado.mensaje,
                    onReintentar = { viewModel.cargar(anuncioId) },
                    onCerrar = onCerrar
                )
                is PagoNequiUiState.Exito -> EstadoExito(pago = estado.pago, onCerrar = onCerrar)
            }
        }
    }
}

@Composable
private fun EstadoCargando() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator()
            Spacer(Modifier.height(12.dp))
            Text("Generando el pago...")
        }
    }
}

@Composable
private fun EstadoError(mensaje: String, onReintentar: () -> Unit, onCerrar: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize().padding(20.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("No se pudo generar el pago", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(
                mensaje,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onCerrar) { Text("Cerrar") }
                Button(onClick = onReintentar) { Text("Reintentar") }
            }
        }
    }
}

@Composable
private fun EstadoExito(pago: PagoPendienteResponse, onCerrar: () -> Unit) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current

    fun copiar(valor: String) {
        clipboard.setText(AnnotatedString(valor))
        Toast.makeText(context, "Copiado", Toast.LENGTH_SHORT).show()
    }

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
        Text("Transferencia Nequi", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                DatoPago(label = "Destinatario", valor = pago.titular)
                DatoPago(label = "Número Nequi", valor = pago.numero_nequi, onCopiar = { copiar(pago.numero_nequi) })
                DatoPago(label = "Monto", valor = "$${"%,.0f".format(pago.monto)} COP")
                DatoPago(label = "Referencia", valor = pago.referencia, onCopiar = { copiar(pago.referencia) })
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
                        "2. Envía $${"%,.0f".format(pago.monto)} al ${pago.numero_nequi}\n" +
                        "3. En el mensaje de la transferencia escribe: ${pago.referencia}\n" +
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
