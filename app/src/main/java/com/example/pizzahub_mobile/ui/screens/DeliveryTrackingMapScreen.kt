package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme
import kotlinx.coroutines.delay

/**
 * Design-only delivery tracking. This does NOT integrate Google Maps SDK yet. Instead it shows a
 * placeholder "map" and an animated driver marker to simulate movement.
 */
@Composable
fun DeliveryTrackingMapScreen(orderId: String, onBack: () -> Unit, modifier: Modifier = Modifier) {
    val cream = Color(0xFFFFF8EE)
    val brownDark = Color(0xFF4E342E)
    val terracota = Color(0xFFD35400)

    // Simulated position (0..1) across the placeholder map. We'll animate it periodically.
    var pos by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(orderId) {
        // simple loop to simulate driver approaching
        while (pos < 0.98f) {
            delay(1200)
            pos = (pos + 0.12f).coerceAtMost(0.98f)
        }
    }

    Column(modifier = modifier.fillMaxSize().background(cream)) {
        // Local top bar
        Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text(
                    text = "Seguimiento",
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Box(
                modifier =
                        Modifier.padding(16.dp)
                                .fillMaxWidth()
                                .height(360.dp)
                                .clip(MaterialTheme.shapes.medium)
                                .background(Color(0xFFDDEAF1)),
                contentAlignment = Alignment.Center
        ) {
            // placeholder "map" area
            Text(text = "Mapa (vista previa)", color = brownDark.copy(alpha = 0.7f))

            // driver marker layer
            Box(modifier = Modifier.fillMaxSize().padding(24.dp)) {
                val x = pos
                // position driver across the width
                Box(modifier = Modifier.fillMaxSize()) {
                    val startOffset = 0.dp
                    val endOffset = 200.dp
                    val currentOffset =
                            (startOffset.value + (endOffset.value - startOffset.value) * x).dp
                    Row(modifier = Modifier.fillMaxHeight().padding(top = 40.dp)) {
                        Spacer(modifier = Modifier.width(currentOffset))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                    Icons.Filled.Place,
                                    contentDescription = "Repartidor",
                                    tint = terracota,
                                    modifier = Modifier.size(36.dp)
                            )
                            Surface(
                                    shape = CircleShape,
                                    color = Color.White,
                                    tonalElevation = 2.dp
                            ) {
                                Text(
                                        text = "Repartidor",
                                        modifier =
                                                Modifier.padding(
                                                        horizontal = 8.dp,
                                                        vertical = 4.dp
                                                ),
                                        color = brownDark,
                                        fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Simple status + ETA
        Card(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                        text = "Pedido #$orderId",
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                        color = brownDark
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text = "Estado: EN_CAMINO",
                        color = terracota,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                val eta = ((1 - pos) * 25).toInt().coerceAtLeast(1)
                Text(text = "Tiempo estimado: ~${eta} min", color = brownDark.copy(alpha = 0.8f))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeliveryTrackingPreview() {
    PizzaHub_MobileTheme { DeliveryTrackingMapScreen(orderId = "o1", onBack = {}) }
}
