package com.example.pizzahub_mobile.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.pizzahub_mobile.MainActivity
import com.example.pizzahub_mobile.R
import com.example.pizzahub_mobile.data.models.EstadoPedido

/** Helper para crear y mostrar notificaciones push de seguimiento de pedidos */
object NotificationHelper {
    private const val CHANNEL_ID_PEDIDOS = "pedidos_channel"
    private const val CHANNEL_NAME = "Seguimiento de Pedidos"
    private const val CHANNEL_DESCRIPTION = "Notificaciones sobre el estado de tus pedidos"

    /** Crea el canal de notificaciones (Android 8.0+) */
    fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel =
                    NotificationChannel(CHANNEL_ID_PEDIDOS, CHANNEL_NAME, importance).apply {
                        description = CHANNEL_DESCRIPTION
                        enableLights(true)
                        enableVibration(true)
                    }

            val notificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    /** Muestra una notificación de actualización de pedido */
    fun showOrderNotification(
            context: Context,
            pedidoId: String,
            numeroPedido: String?,
            estadoPedido: EstadoPedido,
            mensaje: String? = null
    ) {
        createNotificationChannel(context)

        val (title, body, icon) = getNotificationContent(estadoPedido, numeroPedido, mensaje)

        // Intent para abrir la app al hacer clic en la notificación
        val intent =
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra("pedidoId", pedidoId)
                    putExtra("openOrderTracking", true)
                }

        val pendingIntent =
                PendingIntent.getActivity(
                        context,
                        pedidoId.hashCode(),
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

        val notification =
                NotificationCompat.Builder(context, CHANNEL_ID_PEDIDOS)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSmallIcon(icon)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setContentIntent(pendingIntent)
                        .setStyle(NotificationCompat.BigTextStyle().bigText(body))
                        .build()

        val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(pedidoId.hashCode(), notification)
    }

    /** Obtiene el contenido de la notificación según el estado del pedido */
    private fun getNotificationContent(
            estado: EstadoPedido,
            numeroPedido: String?,
            mensaje: String?
    ): Triple<String, String, Int> {
        val pedidoLabel = numeroPedido?.let { "Pedido #$it" } ?: "Tu pedido"

        return when (estado) {
            EstadoPedido.PENDIENTE -> {
                Triple(
                        "🍕 Pedido Recibido",
                        mensaje ?: "$pedidoLabel ha sido recibido y está pendiente de confirmación",
                        R.drawable.ic_launcher_foreground
                )
            }
            EstadoPedido.EN_PREPARACION -> {
                Triple(
                        "👨‍🍳 ¡Tu pizza está en el horno!",
                        mensaje ?: "$pedidoLabel está siendo preparado. ¡Pronto estará lista!",
                        R.drawable.ic_launcher_foreground
                )
            }
            EstadoPedido.EN_CAMINO -> {
                Triple(
                        "🚗 ¡Tu pedido va en camino!",
                        mensaje ?: "$pedidoLabel está en camino a tu domicilio. ¡Llega pronto!",
                        R.drawable.ic_launcher_foreground
                )
            }
            EstadoPedido.ENTREGADO -> {
                Triple(
                        "✅ Pedido Entregado",
                        mensaje ?: "$pedidoLabel ha sido entregado. ¡Buen provecho! 🎉",
                        R.drawable.ic_launcher_foreground
                )
            }
            EstadoPedido.CANCELADO -> {
                Triple(
                        "❌ Pedido Cancelado",
                        mensaje ?: "$pedidoLabel ha sido cancelado",
                        R.drawable.ic_launcher_foreground
                )
            }
        }
    }

    /** Muestra una notificación genérica */
    fun showGenericNotification(context: Context, title: String, body: String) {
        createNotificationChannel(context)

        val notification =
                NotificationCompat.Builder(context, CHANNEL_ID_PEDIDOS)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setAutoCancel(true)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .build()

        val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }
}
