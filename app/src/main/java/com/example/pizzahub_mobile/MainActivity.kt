package com.example.pizzahub_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.pizzahub_mobile.notifications.NotificationHelper
import com.example.pizzahub_mobile.ui.navigation.AppNavHost
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Crear canal de notificaciones
        NotificationHelper.createNotificationChannel(this)

        // Obtener datos de navegación desde la notificación
        val pedidoId = intent?.getStringExtra("pedidoId")
        val openOrderTracking = intent?.getBooleanExtra("openOrderTracking", false) ?: false

        setContent {
            PizzaHub_MobileTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavHost(
                            modifier = Modifier.padding(innerPadding),
                            initialPedidoId = if (openOrderTracking) pedidoId else null
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: android.content.Intent) {
        super.onNewIntent(intent)
        // Manejar nuevas notificaciones mientras la app está en foreground
        val pedidoId = intent.getStringExtra("pedidoId")
        val openOrderTracking = intent.getBooleanExtra("openOrderTracking", false)

        if (openOrderTracking && pedidoId != null) {
            // Aquí podrías usar un evento para navegar, pero por simplicidad
            // recreamos la actividad con los nuevos datos
            setIntent(intent)
            recreate()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainActivityPreview() {
    PizzaHub_MobileTheme { AppNavHost() }
}
