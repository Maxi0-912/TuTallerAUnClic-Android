package com.manuel.tutalleraunclic.notificaciones

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.manuel.tutalleraunclic.R
import com.manuel.tutalleraunclic.data.local.TokenManager
import com.manuel.tutalleraunclic.data.repository.MainRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FCM"

/** Mismo canal que ya crea MainActivity.crearCanalNotificaciones() — no crear uno nuevo acá. */
private const val CANAL_CITAS = "citas"

@AndroidEntryPoint
class MyFirebaseService : FirebaseMessagingService() {

    @Inject lateinit var tokenManager: TokenManager
    @Inject lateinit var mainRepository: MainRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Nuevo token FCM: $token")
        if (tokenManager.hasValidSession()) {
            serviceScope.launch { mainRepository.registrarTokenFcm(token) }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Mensaje recibido, data=${remoteMessage.data}")

        // tipo: cita_creada|cita_confirmada|cita_finalizada|cita_cancelada|anuncio_aprobado|anuncio_rechazado
        val tipo = remoteMessage.data["tipo"]
        val refId = remoteMessage.data["cita_id"] ?: remoteMessage.data["anuncio_id"]
        val titulo = remoteMessage.data["titulo"] ?: "Cita"
        val cuerpo = remoteMessage.data["cuerpo"] ?: "Tenés una actualización en tus citas"

        mostrarNotificacion(titulo, cuerpo, tipo, refId)
    }

    private fun mostrarNotificacion(titulo: String, cuerpo: String, tipo: String?, refId: String?) {
        val notification = NotificationCompat.Builder(this, CANAL_CITAS)
            .setContentTitle(titulo)
            .setContentText(cuerpo)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            // TODO(paso 4): setContentIntent con el deep link según tipo/refId.
            .build()

        val permisoOtorgado = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED

        if (permisoOtorgado) {
            NotificationManagerCompat.from(this).notify(System.currentTimeMillis().toInt(), notification)
        } else {
            Log.d(TAG, "Notificación recibida pero sin permiso POST_NOTIFICATIONS, no se muestra (tipo=$tipo, ref=$refId)")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}
