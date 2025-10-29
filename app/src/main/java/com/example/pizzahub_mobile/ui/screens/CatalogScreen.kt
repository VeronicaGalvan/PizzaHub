package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahub_mobile.data.sample.SampleData
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme

@Composable
fun CatalogScreen(onBack: () -> Unit, onNavigate: (String) -> Unit, category: String = "all") {
    val terracota = Color(0xFFD35400)
    val brownDark = Color(0xFF4E342E)
    val cream = Color(0xFFFFF8EE)

    Column(
            modifier =
                    Modifier.fillMaxSize()
                            .background(cream)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // Header
        Row(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = brownDark
                )
            }

            Text(
                    text = "Catálogo",
                    color = brownDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
            )

            IconButton(onClick = { onNavigate("order") }) {
                Icon(
                        imageVector = Icons.Filled.ShoppingCart,
                        contentDescription = "Cart",
                        tint = brownDark
                )
            }
        }

        // Filtrar productos por categoría
        val items =
                when (category.lowercase()) {
                    "pizzas" -> SampleData.pizzas
                    "bebidas", "drinks", "beverages" -> SampleData.beverages
                    "complementos", "complements" -> SampleData.complements
                    else -> SampleData.pizzas + SampleData.beverages + SampleData.complements
                }

        // Lista de productos
        LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 20.dp)
        ) {
            items(items) { product ->
                ProductItemCard(
                        product.name,
                        product.price.toString(),
                        onClick = {
                            // en el futuro podría navegar al detalle
                        }
                )
            }
        }
    }
}

@Composable
fun ProductItemCard(name: String, price: String, onClick: () -> Unit) {
    val brownDark = Color(0xFF4E342E)
    val softBeige = Color(0xFFFFEFD5)

    Card(
            modifier =
                    Modifier.fillMaxWidth()
                            .shadow(4.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { onClick() },
            colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            // Imagen temporal
            Box(
                    modifier =
                            Modifier.size(80.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(softBeige),
                    contentAlignment = Alignment.Center
            ) { Text(text = "🍕", fontSize = 40.sp) }

            Spacer(modifier = Modifier.width(16.dp))

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Text(text = name, color = brownDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                        text = "$$price",
                        color = brownDark.copy(alpha = 0.8f),
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                )
            }

            // Botón agregar (futuro)
            Button(
                    onClick = onClick,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD35400))
            ) { Text("Agregar", color = Color.White) }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CatalogScreenPreview() {
    PizzaHub_MobileTheme { CatalogScreen(onBack = {}, onNavigate = {}, category = "pizzas") }
}
