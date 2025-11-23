package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme
import com.example.pizzahub_mobile.ui.viewmodel.RatingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingScreen(orderId: String, onBack: () -> Unit, viewModel: RatingViewModel = viewModel()) {
    val brownDark = Color(0xFF4E342E)
    val terracota = Color(0xFFD35400)
    val cream = Color(0xFFFFF8EE)
    val softBeige = Color(0xFFFFF2D5)

    var stars by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }

    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val success by viewModel.success.collectAsState()

    // Navegar hacia atrás cuando la calificación se envía exitosamente
    LaunchedEffect(success) {
        if (success) {
            onBack()
        }
    }

    Column(
            modifier =
                    Modifier.fillMaxSize()
                            .background(Brush.verticalGradient(listOf(cream, Color.White)))
                            .padding(16.dp)
    ) {
        // Título centrado
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = brownDark)
            }
            Text(
                    "Calificar pedido",
                    color = brownDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().shadow(3.dp, RoundedCornerShape(14.dp)),
                colors = CardDefaults.cardColors(containerColor = softBeige)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Pedido #$orderId", fontWeight = FontWeight.SemiBold, color = brownDark)
                Spacer(modifier = Modifier.height(12.dp))

                // Selector de estrellas
                Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    for (i in 1..5) {
                        IconButton(
                                onClick = { stars = i },
                                modifier = Modifier.size(44.dp),
                                enabled = !isLoading
                        ) {
                            Icon(
                                    Icons.Filled.Star,
                                    contentDescription = "Star $i",
                                    tint = if (i <= stars) terracota else Color.LightGray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Comentario") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        colors =
                                OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = terracota,
                                        cursorColor = terracota
                                )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Mostrar error si existe
                if (error != null) {
                    Text(
                            text = error!!,
                            color = Color.Red,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                Button(
                        onClick = {
                            orderId.toIntOrNull()?.let { id ->
                                viewModel.submitCalificacion(id, stars, comment)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = terracota),
                        shape = RoundedCornerShape(16.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(
                            if (isLoading) "Enviando..." else "Enviar reseña",
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
fun RatingPreview() {
    PizzaHub_MobileTheme { RatingScreen(orderId = "o1", onBack = {}) }
}
