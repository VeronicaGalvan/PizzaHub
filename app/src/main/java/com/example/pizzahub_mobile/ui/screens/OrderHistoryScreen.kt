package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme

data class FakeOrder(val id: String, val date: String, val total: Double, val status: String)

@Composable
fun OrderHistoryScreen(
        onBack: () -> Unit,
        onOpenDetail: (String) -> Unit = {},
        onRepeatOrder: (String) -> Unit = {}
) {
    val cream = Color(0xFFFFF8EE)
    val brownDark = Color(0xFF4E342E)

    val orders = remember {
        listOf(
                FakeOrder("o1", "2025-10-20", 45.5, "Entregado"),
                FakeOrder("o2", "2025-10-28", 32.0, "En preparación"),
                FakeOrder("o3", "2025-11-01", 58.25, "Pendiente")
        )
    }

    Column(modifier = Modifier.fillMaxSize().background(cream).padding(16.dp)) {
        TopAppBarSimple(title = "Historial de pedidos", onBack = onBack)

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(orders) { order ->
                Card(
                        modifier = Modifier.fillMaxWidth().clickable { onOpenDetail(order.id) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "Pedido ", color = brownDark.copy(alpha = 0.8f))
                                Text(
                                        text = "#${order.id}",
                                        fontWeight = FontWeight.SemiBold,
                                        color = brownDark
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                    text = order.date,
                                    color = brownDark.copy(alpha = 0.7f),
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color =
                                            when (order.status) {
                                                "Entregado" -> Color(0xFFDFF6E9)
                                                "En preparación" -> Color(0xFFFFF4E8)
                                                else -> Color(0xFFFFF8EE)
                                            },
                                    tonalElevation = 0.dp
                            ) {
                                Text(
                                        text = order.status,
                                        modifier =
                                                Modifier.padding(
                                                        horizontal = 10.dp,
                                                        vertical = 6.dp
                                                ),
                                        color = brownDark,
                                        fontSize = 12.sp
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                    text = "$${"%.2f".format(order.total)}",
                                    fontWeight = FontWeight.Bold,
                                    color = brownDark
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                TextButton(onClick = { onRepeatOrder(order.id) }) {
                                    Icon(
                                            Icons.Filled.Refresh,
                                            contentDescription = "Repetir",
                                            tint = Color(0xFFD35400)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(text = "Repetir", color = Color(0xFFD35400))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopAppBarSimple(title: String, onBack: () -> Unit) {
    val brownDark = Color(0xFF4E342E)
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
            Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = brownDark
            )
        }
        Text(text = title, fontWeight = FontWeight.Bold, color = brownDark)
    }
}

@Preview(showBackground = true)
@Composable
fun OrderHistoryPreview() {
    PizzaHub_MobileTheme { OrderHistoryScreen(onBack = {}) }
}
