package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizzahub_mobile.data.models.Product
import com.example.pizzahub_mobile.data.sample.SampleData
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
        items: List<Product> = SampleData.pizzas,
        onBack: () -> Unit,
        onShowMap: () -> Unit = {},
        onConfirmOrder: () -> Unit = {}
) {
    val cream = Color(0xFFFFF8EE)
    val brownDark = Color(0xFF4E342E)

    val subtotal = items.sumOf { it.price }
    val tax = subtotal * 0.12
    val total = subtotal + tax

    Column(modifier = Modifier.fillMaxSize().background(cream).padding(16.dp)) {
        TopAppBar(
                title = { Text("Confirmación de pedido", color = brownDark) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                                imageVector =
                                        androidx.compose.material.icons.Icons.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = brownDark
                        )
                    }
                }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text("Tipo de pedido", fontWeight = FontWeight.Bold, color = brownDark)
                Spacer(modifier = Modifier.height(8.dp))
                var selected by remember { mutableStateOf("Domicilio") }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    FilterChip(
                            selected = selected == "Domicilio",
                            onClick = { selected = "Domicilio" },
                            label = { Text("Domicilio") }
                    )
                    FilterChip(
                            selected = selected == "Recoger",
                            onClick = { selected = "Recoger" },
                            label = { Text("Recoger") }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Dirección de entrega", fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                        value = "Calle Falsa 123",
                        onValueChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                )

                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onShowMap, modifier = Modifier.fillMaxWidth()) {
                    Text("Ver ruta estimada")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(12.dp)) {
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
                    Text("Total", fontWeight = FontWeight.Bold, color = brownDark)
                    Text(
                            "$${"%.2f".format(total)}",
                            fontWeight = FontWeight.Bold,
                            color = brownDark
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Button(
                        onClick = onConfirmOrder,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B))
                ) { Text("Confirmar pedido", color = Color.White) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CheckoutScreenPreview() {
    PizzaHub_MobileTheme { CheckoutScreen(onBack = {}, onShowMap = {}, onConfirmOrder = {}) }
}
