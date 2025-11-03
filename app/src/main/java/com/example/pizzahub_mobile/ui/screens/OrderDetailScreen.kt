package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahub_mobile.data.sample.SampleData
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme

@Composable
fun OrderDetailScreen(
        orderId: String,
        onBack: () -> Unit,
        onRepeat: (String) -> Unit = {},
        onRate: (String) -> Unit = {}
) {
    val cream = Color(0xFFFFF8EE)
    val brownDark = Color(0xFF4E342E)

    // For design-only, show SampleData items
    val items = remember { SampleData.pizzas }
    val subtotal = items.sumOf { it.price }

    Column(modifier = Modifier.fillMaxSize().background(cream).padding(16.dp)) {
        TopAppBarSimple(title = "Detalle del pedido", onBack = onBack)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                                text = "Pedido #${orderId}",
                                fontWeight = FontWeight.SemiBold,
                                color = brownDark
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                                text = "Estado: Pendiente",
                                color = brownDark.copy(alpha = 0.7f),
                                fontSize = 13.sp
                        )
                    }
                    Text(
                            text = "$${"%.2f".format(subtotal)}",
                            fontWeight = FontWeight.Bold,
                            color = brownDark,
                            textAlign = TextAlign.End
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Column(modifier = Modifier.fillMaxWidth()) {
                    items.forEach { p ->
                        Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                    modifier =
                                            Modifier.size(56.dp)
                                                    .background(
                                                            Color(0xFFFFF2D5),
                                                            shape = RoundedCornerShape(10.dp)
                                                    ),
                                    contentAlignment = Alignment.Center
                            ) { Text("🍕") }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                        text = p.name,
                                        color = brownDark,
                                        fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                        text = p.description ?: "",
                                        color = brownDark.copy(alpha = 0.7f),
                                        fontSize = 12.sp
                                )
                            }
                            Text(text = "$${"%.2f".format(p.price)}", color = brownDark)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "Subtotal", color = brownDark.copy(alpha = 0.8f))
                    Text(
                            text = "$${"%.2f".format(subtotal)}",
                            fontWeight = FontWeight.Bold,
                            color = brownDark
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                            onClick = { onRepeat(orderId) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF4E8))
                    ) {
                        Icon(
                                Icons.Filled.Refresh,
                                contentDescription = "Repetir",
                                tint = Color(0xFFD35400)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Repetir", color = Color(0xFFD35400))
                    }
                    Button(
                            onClick = { onRate(orderId) },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD35400))
                    ) { Text("Calificar", color = Color.White) }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OrderDetailPreview() {
    PizzaHub_MobileTheme { OrderDetailScreen(orderId = "o1", onBack = {}) }
}
