package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme
import kotlinx.coroutines.delay

@Composable
fun DeliveryTrackingMapScreen(orderId: String, onBack: () -> Unit) {
    val cream = Color(0xFFFFF8EE)
    val brownDark = Color(0xFF4E342E)
    val terracota = Color(0xFFD35400)
    val softBeige = Color(0xFFFFEEDD)

    var pos by remember { mutableStateOf(0f) }

    LaunchedEffect(orderId) {
        while (pos < 0.98f) {
            delay(1000)
            pos = (pos + 0.15f).coerceAtMost(0.98f)
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(cream)) {
        Box(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = brownDark)
            }
            Text("Seguimiento", color = brownDark, fontWeight = FontWeight.Bold)
        }

        Box(
                modifier =
                        Modifier.padding(16.dp)
                                .fillMaxWidth()
                                .height(360.dp)
                                .clip(RoundedCornerShape(18.dp))
                                .background(softBeige),
                contentAlignment = Alignment.Center
        ) {
            Text("🗺️ Vista previa del mapa", color = brownDark.copy(alpha = 0.7f))
            Row(
                    modifier =
                            Modifier.fillMaxSize()
                                    .padding(horizontal = (pos * 200).dp, vertical = 140.dp),
                    horizontalArrangement = Arrangement.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Place, contentDescription = "Repartidor", tint = terracota)
                    Surface(shape = CircleShape, color = Color.White, tonalElevation = 2.dp) {
                        Text(
                                "Repartidor",
                                modifier = Modifier.padding(6.dp),
                                color = brownDark,
                                fontSize = 12.sp
                        )
                    }
                }
            }
        }

        Card(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = softBeige)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Pedido #$orderId", fontWeight = FontWeight.SemiBold, color = brownDark)
                Spacer(modifier = Modifier.height(6.dp))
                Text("Estado: EN CAMINO", color = terracota, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                val eta = ((1 - pos) * 25).toInt().coerceAtLeast(1)
                Text("Tiempo estimado: ~${eta} min", color = brownDark.copy(alpha = 0.8f))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DeliveryTrackingPreview() {
    PizzaHub_MobileTheme { DeliveryTrackingMapScreen(orderId = "o1", onBack = {}) }
}
