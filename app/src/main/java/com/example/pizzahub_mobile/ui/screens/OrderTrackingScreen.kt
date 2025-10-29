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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
fun OrderTrackingScreen(onBack: () -> Unit) {
    val terracota = Color(0xFFC0392B)
    val cream = Color(0xFFFFF4E8)
    val brownDark = Color(0xFF4E342E)

    // demo current step: 1 = preparación, 2 = en camino, 3 = entregado
    val currentStep = 2

    Column(modifier = Modifier.fillMaxSize().background(cream).padding(16.dp)) {
        // Header
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) {
                Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = brownDark
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Text(
                        text = "Tu pedido",
                        style = MaterialTheme.typography.titleLarge,
                        color = brownDark,
                        fontWeight = FontWeight.Bold
                )
                Text(
                        text = "#12345",
                        style = MaterialTheme.typography.bodyMedium,
                        color = brownDark.copy(alpha = 0.7f)
                )
            }
            // small status pill
            Surface(shape = RoundedCornerShape(12.dp), color = terracota.copy(alpha = 0.12f)) {
                Text(
                        text = "En camino",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        color = terracota
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Order summary
        Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                // items preview (emoji)
                Box(
                        modifier =
                                Modifier.size(64.dp)
                                        .background(
                                                Color(0xFFF7EDEA),
                                                shape = RoundedCornerShape(10.dp)
                                        ),
                        contentAlignment = Alignment.Center
                ) { Text(text = "🍕", fontSize = 28.sp) }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                            text = "Pizza Pepperoni x1",
                            style = MaterialTheme.typography.titleMedium,
                            color = brownDark
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                            text = "Entrega estimada: 25-35 min",
                            style = MaterialTheme.typography.bodySmall,
                            color = brownDark.copy(alpha = 0.7f)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                            text = "Total",
                            style = MaterialTheme.typography.bodySmall,
                            color = brownDark.copy(alpha = 0.7f)
                    )
                    Text(
                            text = "$12.50",
                            style = MaterialTheme.typography.titleMedium,
                            color = brownDark,
                            fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress steps
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            StepIndicator(
                    label = "Preparación",
                    step = 1,
                    current = currentStep,
                    brown = brownDark,
                    activeColor = terracota
            )
            StepIndicator(
                    label = "En camino",
                    step = 2,
                    current = currentStep,
                    brown = brownDark,
                    activeColor = terracota
            )
            StepIndicator(
                    label = "Entregado",
                    step = 3,
                    current = currentStep,
                    brown = brownDark,
                    activeColor = terracota
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Short status text
        Text(
                text =
                        when (currentStep) {
                            1 -> "Tu pedido está en preparación."
                            2 -> "Tu pedido está en camino."
                            else -> "Pedido entregado"
                        },
                style = MaterialTheme.typography.bodyMedium,
                color = brownDark
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Delivery info
        Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                        modifier =
                                Modifier.size(56.dp)
                                        .background(Color(0xFFF7EDEA), shape = CircleShape),
                        contentAlignment = Alignment.Center
                ) { Text(text = "🚴", fontSize = 24.sp) }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                            text = "Repartidor: Mateo R.",
                            style = MaterialTheme.typography.titleSmall,
                            color = brownDark,
                            fontWeight = FontWeight.SemiBold
                    )
                    Text(
                            text = "Moto - 20 min",
                            style = MaterialTheme.typography.bodySmall,
                            color = brownDark.copy(alpha = 0.7f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { /* call */}) {
                        Icon(
                                imageVector = Icons.Filled.Phone,
                                contentDescription = "Call",
                                tint = terracota
                        )
                    }
                    IconButton(onClick = { /* locate */}) {
                        Icon(
                                imageVector = Icons.Filled.Place,
                                contentDescription = "Locate",
                                tint = terracota
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Actions
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                    onClick = { /* ver detalles */},
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White)
            ) { Text(text = "Ver pedido", color = brownDark) }
            Button(
                    onClick = { /* contactar */},
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = terracota)
            ) { Text(text = "Contactar", color = Color.White) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderTrackingScreenPreview() {
    PizzaHub_MobileTheme { OrderTrackingScreen(onBack = {}) }
}

@Composable
private fun StepIndicator(
        label: String,
        step: Int,
        current: Int,
        brown: Color,
        activeColor: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        val isActive = step <= current
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(52.dp)) {
            Canvas(modifier = Modifier.size(52.dp)) {
                drawCircle(
                        color = if (isActive) activeColor else Color(0xFFF4E6E0),
                        radius = size.minDimension / 2
                )
            }
            if (isActive) Text(text = "", color = Color.White)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = brown,
                fontSize = 12.sp
        )
    }
}
