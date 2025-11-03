package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme

@Composable
fun OrderTrackingScreen(
        onBack: () -> Unit,
        orderId: String = "#12345",
        onOpenMap: (String) -> Unit = {}
) {
        val terracota = Color(0xFFD35400)
        val cream = Color(0xFFFFF8EE)
        val brownDark = Color(0xFF4E342E)
        val softBeige = Color(0xFFFFEEDD)

        val currentStep = 2 // demo

        Column(modifier = Modifier.fillMaxSize().background(cream).padding(16.dp)) {
                // Header centrado
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        IconButton(
                                onClick = onBack,
                                modifier = Modifier.align(Alignment.CenterStart)
                        ) {
                                Icon(
                                        Icons.Filled.ArrowBack,
                                        contentDescription = "Volver",
                                        tint = brownDark
                                )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                        "Tu pedido",
                                        color = brownDark,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp
                                )
                                Text(
                                        orderId,
                                        color = brownDark.copy(alpha = 0.7f),
                                        fontSize = 13.sp
                                )
                        }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Card resumen
                Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = softBeige),
                        modifier = Modifier.fillMaxWidth()
                ) {
                        Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Box(
                                        modifier =
                                                Modifier.size(64.dp)
                                                        .background(
                                                                Color(0xFFF6E1C3),
                                                                RoundedCornerShape(10.dp)
                                                        ),
                                        contentAlignment = Alignment.Center
                                ) { Text("🍕", fontSize = 28.sp) }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                                "Pizza Pepperoni x1",
                                                color = brownDark,
                                                fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                                "Entrega estimada: 25–35 min",
                                                color = brownDark.copy(alpha = 0.7f)
                                        )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                                "Total",
                                                color = brownDark.copy(alpha = 0.6f),
                                                fontSize = 13.sp
                                        )
                                        Text(
                                                "$12.50",
                                                color = brownDark,
                                                fontWeight = FontWeight.Bold
                                        )
                                }
                        }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Progreso del pedido
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        StepIndicator("Preparación", 1, currentStep, brownDark, terracota)
                        StepIndicator("En camino", 2, currentStep, brownDark, terracota)
                        StepIndicator("Entregado", 3, currentStep, brownDark, terracota)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                        text =
                                when (currentStep) {
                                        1 -> "Tu pedido está en preparación 🧑‍🍳"
                                        2 -> "Tu pedido está en camino 🚗"
                                        else -> "Pedido entregado ✅"
                                },
                        color = brownDark,
                        fontSize = 15.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Info del repartidor
                Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = softBeige),
                        modifier = Modifier.fillMaxWidth()
                ) {
                        Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Box(
                                        modifier =
                                                Modifier.size(56.dp)
                                                        .background(Color(0xFFF6E1C3), CircleShape),
                                        contentAlignment = Alignment.Center
                                ) { Text("🚴", fontSize = 24.sp) }

                                Spacer(modifier = Modifier.width(12.dp))

                                Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                                "Repartidor: Mateo R.",
                                                color = brownDark,
                                                fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                                "Moto - 20 min",
                                                color = brownDark.copy(alpha = 0.7f),
                                                fontSize = 13.sp
                                        )
                                }

                                Row {
                                        IconButton(onClick = { /* call */}) {
                                                Icon(
                                                        Icons.Filled.Phone,
                                                        contentDescription = "Llamar",
                                                        tint = terracota
                                                )
                                        }
                                        IconButton(onClick = { onOpenMap(orderId) }) {
                                                Icon(
                                                        Icons.Filled.Place,
                                                        contentDescription = "Ver mapa",
                                                        tint = terracota
                                                )
                                        }
                                }
                        }
                }

                Spacer(modifier = Modifier.weight(1f))

                // Botones inferiores
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                        OutlinedButton(
                                onClick = { /* ver pedido */},
                                modifier = Modifier.weight(1f),
                                colors =
                                        ButtonDefaults.outlinedButtonColors(
                                                contentColor = terracota
                                        )
                        ) { Text("Ver pedido") }

                        Button(
                                onClick = { /* contactar */},
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = terracota)
                        ) { Text("Contactar", color = Color.White) }
                }
        }
}

@Composable
private fun StepIndicator(
        label: String,
        step: Int,
        current: Int,
        brown: Color,
        activeColor: Color
) {
        val isActive = step <= current
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(contentAlignment = Alignment.Center) {
                        Canvas(modifier = Modifier.size(48.dp)) {
                                drawCircle(
                                        color = if (isActive) activeColor else Color(0xFFF4E6E0),
                                        radius = size.minDimension / 2
                                )
                        }
                        if (isActive && step < current)
                                Text("✓", color = Color.White, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(label, color = brown, fontSize = 12.sp)
        }
}

@Preview(showBackground = true)
@Composable
fun OrderTrackingScreenPreview() {
        PizzaHub_MobileTheme { OrderTrackingScreen(onBack = {}) }
}
