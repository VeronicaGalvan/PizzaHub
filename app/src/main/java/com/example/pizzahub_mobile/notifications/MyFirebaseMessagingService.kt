package com.example.pizzahub_mobile.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.pizzahub_mobile.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Simple FirebaseMessagingService that logs token, persists it if desired, and shows a simple
 * notification for incoming messages. Ready for manual tests from Firebase Console.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val TAG = "MyFirebaseMsgService"
    private val CHANNEL_ID = "pizzahub_notifications"

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "FCM new token: $token")
        // Optionally persist the FCM token somewhere, or upload to your backend.
        // Example: save to DataStore (non-blocking)
        val ctx = applicationContext
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // You can save this token using your TokenDataStore or a separate store
                // TokenDataStore.saveFcmToken(ctx, token) // not implemented
            } catch (e: Exception) {
                Log.e(TAG, "Failed to persist FCM token: ${e.localizedMessage}")
            }
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d(TAG, "Message received from: ${remoteMessage.from}")

        val title = remoteMessage.notification?.title ?: remoteMessage.data["title"] ?: "PizzaHub"
        val body =
                remoteMessage.notification?.body
                        ?: remoteMessage.data["body"] ?: "Tienes una actualización sobre tu pedido"

        showNotification(this, title, body)
    }

    private fun showNotification(context: Context, title: String, body: String) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                    NotificationChannel(
                            CHANNEL_ID,
                            "PizzaHub notifications",
                            NotificationManager.IMPORTANCE_DEFAULT
                    )
            nm.createNotificationChannel(channel)
        }

        val notification =
                NotificationCompat.Builder(context, CHANNEL_ID)
                        .setContentTitle(title)
                        .setContentText(body)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .build()

        nm.notify(System.currentTimeMillis().toInt(), notification)
    }
}
