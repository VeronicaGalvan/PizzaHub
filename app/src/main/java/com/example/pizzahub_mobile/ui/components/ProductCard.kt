package com.example.pizzahub_mobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.pizzahub_mobile.data.models.Product
import com.example.pizzahub_mobile.ui.theme.PizzaBrown
import com.example.pizzahub_mobile.ui.theme.PizzaRed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun ProductCard(product: Product, modifier: Modifier = Modifier, isHighlighted: Boolean = false, onClick: (() -> Unit)? = null) {
    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = if (isHighlighted) Color(0xFFFFF0EB) else Color.White)
    ) {
        Row(modifier = Modifier
            .padding(14.dp)
            .fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier
                    .size(84.dp)
                    .clip(CircleShape),
                color = Color(0xFFF7EDEA)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = "🍕", fontSize = 28.sp)
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(text = product.name, color = PizzaBrown, fontWeight = FontWeight.SemiBold)
                product.description?.let {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = it, color = Color(0xFF8A8A8A), fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(text = String.format("$%.0f", product.price), color = PizzaRed, fontWeight = FontWeight.Bold)
        }
    }
}

// pequeño helper para color de placeholder (evita dependencia en Theme.kt)
// pequeño helper para color de placeholder (evita dependencia en Theme.kt)
private fun PizzaLight(): Color = Color(0xFFDFAE84)

