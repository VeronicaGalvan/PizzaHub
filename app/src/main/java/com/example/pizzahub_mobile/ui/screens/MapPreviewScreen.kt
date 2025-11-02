package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPreviewScreen(onBack: () -> Unit, onConfirm: () -> Unit = {}) {
    val cream = Color(0xFFFFF8EE)
    val brownDark = Color(0xFF4E342E)
    val terracota = Color(0xFFD35400)
    val softBeige = Color(0xFFFFF2D5)

    Column(
            modifier =
                    Modifier.fillMaxSize()
                            .background(Brush.verticalGradient(listOf(cream, Color.White)))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Header with centered title
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = brownDark)
            }

            Text(
                    text = "Ruta estimada",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = brownDark
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = softBeige),
                modifier =
                        Modifier.fillMaxWidth().weight(1f).shadow(4.dp, RoundedCornerShape(20.dp))
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "🗺️ Vista previa del mapa", color = Color.Gray, fontSize = 18.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
                onClick = onConfirm,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = terracota)
        ) { Text("Volver al checkout", color = Color.White, fontWeight = FontWeight.Bold) }
    }
}

@Preview(showBackground = true)
@Composable
fun MapPreviewScreenPreview() {
    PizzaHub_MobileTheme { MapPreviewScreen(onBack = {}, onConfirm = {}) }
}
