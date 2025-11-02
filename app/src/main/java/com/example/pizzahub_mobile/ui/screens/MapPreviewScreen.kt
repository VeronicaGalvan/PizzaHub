package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapPreviewScreen(onBack: () -> Unit, onConfirm: () -> Unit = {}) {
    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFFF8EE)).padding(16.dp)) {
        TopAppBar(
                title = { Text("Ruta estimada", color = Color(0xFF4E342E)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = Color(0xFF4E342E)
                        )
                    }
                }
        )

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            // Placeholder map view
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "[Vista previa del mapa]", fontSize = 18.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(12.dp))
                Button(onClick = onConfirm) { Text("Volver al checkout") }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapPreviewScreenPreview() {
    PizzaHub_MobileTheme { MapPreviewScreen(onBack = {}, onConfirm = {}) }
}
