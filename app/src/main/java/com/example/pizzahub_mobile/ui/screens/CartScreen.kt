package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahub_mobile.data.sample.SampleData
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme
import com.example.pizzahub_mobile.ui.viewmodel.CartItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
        itemsWithQty: List<CartItem> = SampleData.pizzas.map { CartItem(it, 1) },
        onBack: () -> Unit,
        onUpdateQuantity: (String, Int) -> Unit = { _, _ -> },
        onRemoveItem: (String) -> Unit = {},
        onClearCart: () -> Unit = {},
        onProceedToCheckout: () -> Unit = {}
) {
    val cream = Color(0xFFFFF8EE)
    val brownDark = Color(0xFF4E342E)
    val terracota = Color(0xFFD35400)
    val softBeige = Color(0xFFFFF2D5)

    // quantities are provided by itemsWithQty

    Column(
            modifier =
                    Modifier.fillMaxSize()
                            .background(cream)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Header: back button left, centered title, action right
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = brownDark)
            }

            Text(
                    text = "Tu Carrito",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = brownDark
            )

            TextButton(onClick = onClearCart, modifier = Modifier.align(Alignment.CenterEnd)) {
                Text("Vaciar", color = terracota, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (itemsWithQty.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tu carrito está vacío 🍕", color = brownDark, fontSize = 16.sp)
            }
            return
        }

        // Product list
        LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
        ) {
            items(itemsWithQty) { ci ->
                Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = softBeige),
                        modifier = Modifier.fillMaxWidth().shadow(2.dp, RoundedCornerShape(18.dp))
                ) {
                    Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        val p = ci.product
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                    p.name,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = brownDark
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("$${"%.2f".format(p.price)}", color = brownDark.copy(alpha = 0.8f))
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                    onClick = {
                                        val new = (ci.quantity).coerceAtLeast(2) - 1
                                        onUpdateQuantity(ci.product.id, new)
                                    }
                            ) { Text("-", color = brownDark, fontSize = 20.sp) }

                            Text(
                                    (ci.quantity).toString(),
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.width(28.dp),
                                    color = brownDark
                            )

                            IconButton(
                                    onClick = {
                                        val new = (ci.quantity) + 1
                                        onUpdateQuantity(ci.product.id, new)
                                    }
                            ) { Text("+", color = brownDark, fontSize = 20.sp) }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                    "Eliminar",
                                    color = Color.Red,
                                    modifier =
                                            Modifier.clickable { onRemoveItem(ci.product.id) }
                                                    .padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Footer summary
        val subtotal = itemsWithQty.sumOf { it.product.price * it.quantity }
        val tax = subtotal * 0.12
        val total = subtotal + tax

        Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = softBeige),
                modifier = Modifier.shadow(4.dp, RoundedCornerShape(22.dp))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                SummaryRow("Subtotal", subtotal, brownDark)
                SummaryRow("Impuestos", tax, brownDark)
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                SummaryRow("Total", total, brownDark, true)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                        onClick = onProceedToCheckout,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = terracota)
                ) { Text("Proceder al pago", color = Color.White, fontWeight = FontWeight.Bold) }
            }
        }
    }
}

@Composable
fun SummaryRow(label: String, value: Double, color: Color, bold: Boolean = false) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = color, fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal)
        Text(
                "$${"%.2f".format(value)}",
                color = color,
                fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    PizzaHub_MobileTheme { CartScreen(onBack = {}, onProceedToCheckout = {}) }
}
