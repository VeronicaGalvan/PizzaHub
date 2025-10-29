package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizzahub_mobile.ui.theme.PizzaBrown
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme
import com.example.pizzahub_mobile.ui.theme.PizzaRed

@Composable
fun OrderTrackingScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        // Top bar con botón de retroceso para usar onBack
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Tu pedido", style = MaterialTheme.typography.titleLarge, color = PizzaBrown)
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column { Text("Preparación") }
            Column { Text("En camino") }
            Column { Text("Entregado") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
                modifier = Modifier.height(260.dp).fillMaxWidth(),
                contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // ruta punteada
                val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                val start = Offset(size.width * 0.15f, size.height * 0.6f)
                val mid = Offset(size.width * 0.5f, size.height * 0.3f)
                val end = Offset(size.width * 0.85f, size.height * 0.6f)
                drawLine(
                        color = Color(0xFFDCC6BC),
                        start = start,
                        end = mid,
                        strokeWidth = 6f,
                        pathEffect = pathEffect
                )
                drawLine(
                        color = Color(0xFFDCC6BC),
                        start = mid,
                        end = end,
                        strokeWidth = 6f,
                        pathEffect = pathEffect
                )
                // marcadores
                drawCircle(color = Color(0xFFF4E6E0), radius = 18f, center = start)
                drawCircle(color = Color(0xFFB94E3A), radius = 18f, center = mid)
                drawCircle(color = Color(0xFFF4E6E0), radius = 18f, center = end)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        Text(
                "Tu pedido está en camino.",
                style = MaterialTheme.typography.titleMedium,
                color = PizzaBrown
        )

        Spacer(modifier = Modifier.weight(1f))

        // Botón inferior tipo carrito
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = PizzaRed,
                    modifier = Modifier.height(56.dp).fillMaxWidth(0.6f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = "cart",
                            tint = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderTrackingScreenPreview() {
    PizzaHub_MobileTheme { OrderTrackingScreen(onBack = {}) }
}
