package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahub_mobile.ui.components.PrimaryButton
import com.example.pizzahub_mobile.ui.theme.PizzaBrown
import com.example.pizzahub_mobile.ui.theme.PizzaCream
import com.example.pizzahub_mobile.ui.theme.PizzaGray
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    Column(
            modifier = Modifier.fillMaxSize().background(PizzaCream).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onNavigate("profile") }) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
            }
            Text(text = "PizzaHub", style = MaterialTheme.typography.titleLarge, color = PizzaBrown)
            IconButton(onClick = { onNavigate("order") }) {
                Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Carrito")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Imagen circular grande (placeholder)
        Surface(
                modifier = Modifier.size(220.dp).clip(CircleShape),
                color = Color.White,
                shape = RoundedCornerShape(120.dp)
        ) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "🍕", fontSize = 72.sp)
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.Start) {
            Text(
                    text = "Pizza Deliciosa",
                    style = MaterialTheme.typography.titleLarge,
                    color = PizzaBrown,
                    fontWeight = FontWeight.Bold
            )

            Text(
                    text = "Disfruta la mejor pizza",
                    style = MaterialTheme.typography.bodyLarge,
                    color = PizzaGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                    text = "Ordenar ahora",
                    onClick = { onNavigate("catalog") },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                    text = "Categorías",
                    style = MaterialTheme.typography.titleMedium,
                    color = PizzaBrown,
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
            ) {
                CategoryButton(text = "Pizzas")
                CategoryButton(text = "Bebidas")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PizzaHub_MobileTheme { HomeScreen(onNavigate = {}) }
}

@Composable
fun CategoryButton(text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
                modifier =
                        Modifier.size(72.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFFFFF8F6)),
                contentAlignment = Alignment.Center
        ) { Text(text = text.take(1), color = PizzaBrown, fontWeight = FontWeight.Bold) }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = text, color = PizzaBrown, fontSize = 14.sp)
    }
}
