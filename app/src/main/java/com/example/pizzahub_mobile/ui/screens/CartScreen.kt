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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahub_mobile.data.models.Product
import com.example.pizzahub_mobile.data.sample.SampleData
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme

@Composable
fun CartScreen(
        items: List<Product> = SampleData.pizzas,
        onBack: () -> Unit,
        onUpdateQuantity: (productId: String, newQty: Int) -> Unit = { _, _ -> },
        onRemoveItem: (productId: String) -> Unit = {},
        onClearCart: () -> Unit = {},
        onProceedToCheckout: () -> Unit = {}
) {
    val cream = Color(0xFFFFF8EE)
    val brownDark = Color(0xFF4E342E)

    // Local UI state for quantities (simple client-side mock)
    val quantities = remember { mutableStateMapOf<String, Int>() }
    items.forEach { quantities.putIfAbsent(it.id, 1) }

    @OptIn(ExperimentalMaterial3Api::class)
    Column(modifier = Modifier.fillMaxSize().background(cream).padding(16.dp)) {
        TopAppBar(
                title = { Text("Carrito", color = brownDark) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = brownDark
                        )
                    }
                },
                actions = {
                    TextButton(onClick = { onClearCart() }) {
                        Text("Vaciar carrito", color = brownDark)
                    }
                }
        )

        Spacer(modifier = Modifier.height(12.dp))

        if (items.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Tu carrito está vacío", color = brownDark)
            }
            return@Column
        }

        LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.weight(1f)
        ) {
            items(items) { p ->
                Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = p.name, fontSize = 16.sp, color = brownDark)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = "$${p.price}", color = brownDark.copy(alpha = 0.8f))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                    onClick = {
                                        val current = quantities[p.id] ?: 1
                                        val next = (current - 1).coerceAtLeast(1)
                                        quantities[p.id] = next
                                        onUpdateQuantity(p.id, next)
                                    }
                            ) { Text("-") }
                            Text(
                                    (quantities[p.id] ?: 1).toString(),
                                    modifier = Modifier.width(24.dp),
                                    textAlign = TextAlign.Center
                            )
                            IconButton(
                                    onClick = {
                                        val current = quantities[p.id] ?: 1
                                        val next = current + 1
                                        quantities[p.id] = next
                                        onUpdateQuantity(p.id, next)
                                    }
                            ) { Text("+") }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                    text = "Eliminar",
                                    color = Color.Red,
                                    modifier = Modifier.clickable { onRemoveItem(p.id) }
                            )
                        }
                    }
                }
            }
        }

        // Footer summary
        val subtotal = items.sumOf { it.price * (quantities[it.id] ?: 1) }
        val tax = subtotal * 0.12
        val total = subtotal + tax

        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Subtotal", color = brownDark)
                Text("$${"%.2f".format(subtotal)}", color = brownDark)
            }
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Impuestos", color = brownDark)
                Text("$${"%.2f".format(tax)}", color = brownDark)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                        "Total",
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = brownDark
                )
                Text(
                        "$${"%.2f".format(total)}",
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        color = brownDark
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                    onClick = onProceedToCheckout,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B))
            ) { Text("Proceder al pago", color = Color.White) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CartScreenPreview() {
    PizzaHub_MobileTheme { CartScreen(onBack = {}, onProceedToCheckout = {}) }
}
