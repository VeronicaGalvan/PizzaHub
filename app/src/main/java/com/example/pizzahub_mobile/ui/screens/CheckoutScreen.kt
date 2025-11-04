package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahub_mobile.data.sample.SampleData
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme
import com.example.pizzahub_mobile.ui.viewmodel.CartItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
        itemsWithQty: List<CartItem> = SampleData.pizzas.map { CartItem(it, 1) },
        onBack: () -> Unit,
        onShowMap: () -> Unit = {},
        onConfirmOrder: (orderId: String) -> Unit = {},
        onSelectAddress: () -> Unit = {}
) {
        val cream = Color(0xFFFFF8EE)
        val softBeige = Color(0xFFFFEEDD)
        val brownDark = Color(0xFF4E342E)
        val terracota = Color(0xFFD35400)

        var tipoPedido by remember { mutableStateOf("Domicilio") }

        val subtotal = itemsWithQty.sumOf { it.product.price * it.quantity }
        val tax = subtotal * 0.12
        val total = subtotal + tax

        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .background(Brush.verticalGradient(listOf(cream, Color.White)))
                                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
                // 🔙 Encabezado con título centrado
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

                        Text(
                                text = "Confirmar pedido",
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = brownDark
                        )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 🧾 Tipo de pedido
                Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = softBeige),
                        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp))
                ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                                Text(
                                        "Tipo de pedido",
                                        fontWeight = FontWeight.Bold,
                                        color = brownDark
                                )
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                        FilterChip(
                                                selected = tipoPedido == "Domicilio",
                                                onClick = { tipoPedido = "Domicilio" },
                                                label = { Text("Domicilio") }
                                        )
                                        FilterChip(
                                                selected = tipoPedido == "Recoger",
                                                onClick = { tipoPedido = "Recoger" },
                                                label = { Text("Recoger") }
                                        )
                                }

                                if (tipoPedido == "Domicilio") {
                                        Spacer(modifier = Modifier.height(16.dp))
                                        Text(
                                                "Dirección de entrega",
                                                fontWeight = FontWeight.Medium,
                                                color = brownDark
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        OutlinedTextField(
                                                value = "Calle Falsa 123",
                                                onValueChange = {},
                                                modifier =
                                                        Modifier.fillMaxWidth().clickable {
                                                                onSelectAddress()
                                                        },
                                                readOnly = true
                                        )
                                        Spacer(modifier = Modifier.height(12.dp))
                                        Button(
                                                onClick = onShowMap,
                                                modifier = Modifier.fillMaxWidth(),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor = terracota
                                                        )
                                        ) { Text("Ver ruta estimada", color = Color.White) }
                                }
                        }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 💳 Resumen y confirmación
                Card(
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = softBeige),
                        modifier = Modifier.fillMaxWidth().shadow(4.dp, RoundedCornerShape(20.dp))
                ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                                SummaryRow("Subtotal", subtotal, brownDark)
                                SummaryRow("Impuestos", tax, brownDark)
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                SummaryRow("Total", total, brownDark, bold = true)
                                Spacer(modifier = Modifier.height(16.dp))

                                Button(
                                        onClick = {
                                                val orderId =
                                                        java.util
                                                                .UUID
                                                                .randomUUID()
                                                                .toString()
                                                                .take(8)
                                                onConfirmOrder(orderId)
                                        },
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(16.dp),
                                        colors =
                                                ButtonDefaults.buttonColors(
                                                        containerColor = terracota
                                                )
                                ) {
                                        Text(
                                                "Confirmar pedido",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold
                                        )
                                }
                        }
                }
        }
}

@Preview(showBackground = true)
@Composable
fun CheckoutScreenPreview() {
        PizzaHub_MobileTheme { CheckoutScreen(onBack = {}, onShowMap = {}, onConfirmOrder = {}) }
}
