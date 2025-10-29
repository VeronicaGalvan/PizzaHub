package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme

@Composable
fun HomeScreen(onNavigate: (String) -> Unit) {
    val terracota = Color(0xFFD35400)
    val brownDark = Color(0xFF4E342E)
    val cream = Color(0xFFFFF8EE)
    val softBeige = Color(0xFFFFEFD5)

    // Fondo degradado sutil
    Box(
            modifier =
                    Modifier.fillMaxSize()
                            .background(Brush.verticalGradient(listOf(cream, Color.White)))
                            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Encabezado
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onNavigate("profile") }) {
                    Icon(Icons.Default.Menu, contentDescription = "Menu", tint = brownDark)
                }

                Text(
                        text = "PizzaHub",
                        color = brownDark,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.SansSerif
                )

                IconButton(onClick = { onNavigate("order") }) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = brownDark)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Imagen central (emoji)
            Box(
                    modifier =
                            Modifier.size(240.dp)
                                    .shadow(8.dp, shape = CircleShape, clip = false)
                                    .background(Color.White, shape = CircleShape),
                    contentAlignment = Alignment.Center
            ) { Text(text = "🍕", fontSize = 90.sp) }

            Spacer(modifier = Modifier.height(24.dp))

            // Texto principal
            Text(
                    text = "Pizza Deliciosa",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = brownDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                    text = "Disfruta la mejor pizza",
                    fontSize = 16.sp,
                    color = brownDark.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón principal
            Button(
                    onClick = { onNavigate("catalog") },
                    colors = ButtonDefaults.buttonColors(containerColor = terracota),
                    shape = RoundedCornerShape(14.dp),
                    modifier =
                            Modifier.fillMaxWidth(0.85f)
                                    .height(52.dp)
                                    .shadow(4.dp, RoundedCornerShape(14.dp))
            ) {
                Text(
                        text = "Ordenar ahora",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Categorías
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                Text(
                        text = "Categorías",
                        fontWeight = FontWeight.SemiBold,
                        color = brownDark,
                        fontSize = 18.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            val categories =
                    listOf(Pair("Pizzas", "🍕"), Pair("Bebidas", "🥤"), Pair("Complementos", "🍟"))

            LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(horizontal = 6.dp)
            ) {
                items(categories) { (label, icon) ->
                    CategoryCard(
                            label = label,
                            icon = icon,
                            onClick = {
                                when (label) {
                                    "Pizzas" -> onNavigate("catalog/pizzas")
                                    "Bebidas" -> onNavigate("catalog/bebidas")
                                    "Complementos" -> onNavigate("catalog/complementos")
                                    else -> onNavigate("catalog")
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCard(label: String, icon: String, onClick: () -> Unit) {
    val background = Color(0xFFFFF1D6)
    val textColor = Color(0xFF4E342E)

    Column(
            modifier =
                    Modifier.width(100.dp)
                            .clickable(onClick = onClick)
                            .clip(RoundedCornerShape(18.dp))
                            .background(background)
                            .padding(vertical = 18.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = icon, fontSize = 36.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, color = textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    PizzaHub_MobileTheme { HomeScreen(onNavigate = {}) }
}
