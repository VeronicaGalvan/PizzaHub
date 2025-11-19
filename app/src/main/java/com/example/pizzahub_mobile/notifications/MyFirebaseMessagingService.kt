package com.example.pizzahub_mobile.notifications

import android.util.Log
import com.example.pizzahub_mobile.data.models.EstadoPedido
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * FirebaseMessagingService que maneja notificaciones push de seguimiento de pedidos. El backend
 * enviará notificaciones con datos de estado de pedido.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "MyFirebaseMsgService"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM new token: $token")

        // TODO: Enviar el token al backend para asociarlo con el usuario
        // Esto permitirá que el backend envíe notificaciones push específicas
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Aquí podrías llamar un endpoint como:
                // authRepository.registerFcmToken(token)
                Log.d(TAG, "FCM token ready to be registered with backend")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to register FCM token: ${e.localizedMessage}")
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message received from: ${remoteMessage.from}")
        Log.d(TAG, "Message data: ${remoteMessage.data}")

        // El backend puede enviar datos en el campo 'data' de la notificación
        val data = remoteMessage.data

        // Verificar si es una notificación de pedido
        val tipo = data["tipo"] ?: data["type"]

        when (tipo) {
            "pedido_estado", "order_status" -> {
                handleOrderStatusNotification(data)
            }
            else -> {
                // Notificación genérica
                val title = remoteMessage.notification?.title ?: data["title"] ?: "PizzaHub"
                val body =
                        remoteMessage.notification?.body
                                ?: data["body"] ?: "Tienes una actualización"

                NotificationHelper.showGenericNotification(this, title, body)
            }
        }
    }

    /**
     * Maneja notificaciones de cambio de estado de pedido
     *
     * Datos esperados del backend:
     * - tipo: "pedido_estado" o "order_status"
     * - pedidoId: ID del pedido
     * - numeroPedido: Número de pedido (opcional)
     * - estado: Estado del pedido (puede ser int: 1,2,3... o string: "Pendiente", "En preparación",
     * etc.)
     * - mensaje: Mensaje personalizado (opcional)
     */
    private fun handleOrderStatusNotification(data: Map<String, String>) {
        try {
            val pedidoId = data["pedidoId"] ?: data["orderId"] ?: return
            val numeroPedido = data["numeroPedido"] ?: data["orderNumber"]
            val estadoRaw = data["estado"] ?: data["status"] ?: return
            val mensaje = data["mensaje"] ?: data["message"]

            // Intentar parsear el estado (puede ser int o string)
            val estadoPedido =
                    estadoRaw.toIntOrNull()?.let { EstadoPedido.fromValue(it) }
                            ?: EstadoPedido.fromString(estadoRaw)

            if (estadoPedido != null) {
                Log.d(TAG, "Order notification: pedido=$pedidoId, estado=$estadoPedido")

                NotificationHelper.showOrderNotification(
                        context = this,
                        pedidoId = pedidoId,
                        numeroPedido = numeroPedido,
                        estadoPedido = estadoPedido,
                        mensaje = mensaje
                )
            } else {
                Log.w(TAG, "Unknown order status value: $estadoRaw")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error handling order notification: ${e.localizedMessage}", e)
        }
    }
}
