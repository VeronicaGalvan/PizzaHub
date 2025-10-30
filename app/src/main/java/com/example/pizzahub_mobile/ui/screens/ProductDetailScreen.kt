package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme

@Composable
fun ProductDetailScreen(
        productId: String = "p1",
        onAddToCart: () -> Unit = {},
        onBack: (() -> Unit)? = null
) {
    val terracota = Color(0xFFD35400)
    val brownDark = Color(0xFF4E342E)
    val cream = Color(0xFFFFF8EE)
    val softBeige = Color(0xFFFFEEDD)

    Box(
            modifier =
                    Modifier.fillMaxSize()
                            .background(Brush.verticalGradient(listOf(cream, Color.White)))
    ) {
        Column(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 🔙 Encabezado
            Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onBack?.invoke() }) {
                    Icon(
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = brownDark
                    )
                }

                Text(
                        text = "Detalle del producto",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = brownDark
                )

                IconButton(onClick = { /* Ir al carrito */}) {
                    Icon(
                            imageVector = Icons.Filled.ShoppingCart,
                            contentDescription = "Carrito",
                            tint = brownDark
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 🍕 Imagen principal
            Box(
                    modifier =
                            Modifier.size(220.dp)
                                    .shadow(8.dp, shape = CircleShape)
                                    .background(Color.White, shape = CircleShape),
                    contentAlignment = Alignment.Center
            ) { Text(text = "🍕", fontSize = 96.sp) }

            Spacer(modifier = Modifier.height(28.dp))

            // 🧾 Información del producto
            Text(
                    text = "Pizza Pepperoni Suprema",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = brownDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                    text =
                            "Crujiente masa artesanal con extra queso y pepperoni fresco, horneada al momento para ti.",
                    fontSize = 15.sp,
                    color = brownDark.copy(alpha = 0.85f),
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier =
                            Modifier.fillMaxWidth(0.85f) // centrado real
                                    .align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 💲 Precio + botón añadir
            Card(
                    modifier =
                            Modifier.fillMaxWidth(0.9f) // ✅ más estrecha, centrada
                                    .shadow(8.dp, RoundedCornerShape(24.dp)),
                    colors = CardDefaults.cardColors(containerColor = softBeige),
                    shape = RoundedCornerShape(24.dp)
            ) {
                Column(
                        modifier =
                                Modifier.padding(vertical = 26.dp, horizontal = 20.dp)
                                        .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                            text = "$12.50",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = terracota
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(
                            onClick = onAddToCart,
                            colors = ButtonDefaults.buttonColors(containerColor = terracota),
                            shape = RoundedCornerShape(18.dp),
                            modifier =
                                    Modifier.fillMaxWidth(0.8f)
                                            .height(52.dp)
                                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                                text = "Añadir al carrito",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // 🔄 Botón para seguir comprando (ligero)
            Text(
                    text = "Seguir explorando 🍕",
                    color = terracota,
                    fontSize = 15.sp,
                    modifier = Modifier.clickable { onBack?.invoke() }.padding(top = 4.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProductDetailPreview() {
    PizzaHub_MobileTheme { ProductDetailScreen() }
}
