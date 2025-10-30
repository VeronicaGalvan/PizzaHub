package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme

@Composable
fun ProductDetailScreen(productId: String, onAddToCart: () -> Unit) {
    val cream = Color(0xFFFFF4E8)
    Column(
            modifier = Modifier.fillMaxSize().background(cream).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Producto: $productId", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text = "Deliciosa pizza con pepperoni y queso.",
                        style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "$12.50", style = MaterialTheme.typography.titleMedium)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
                onClick = onAddToCart,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC0392B))
        ) { Text(text = "Añadir al carrito", color = Color.White) }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailPreview() {
    PizzaHub_MobileTheme { ProductDetailScreen(productId = "p1", onAddToCart = {}) }
}
