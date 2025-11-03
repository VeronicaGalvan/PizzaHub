package com.example.pizzahub_mobile.ui.screens

// import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingScreen(
        orderId: String,
        onBack: () -> Unit,
        onSubmit: (stars: Int, comment: String) -> Unit = { _, _ -> }
) {
    val brownDark = Color(0xFF4E342E)
    var stars by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFFFF8EE)).padding(16.dp)) {
        TopAppBarSimple(title = "Calificar pedido", onBack = onBack)
        Spacer(modifier = Modifier.height(12.dp))

        Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "Pedido #$orderId", fontWeight = FontWeight.SemiBold, color = brownDark)
                Spacer(modifier = Modifier.height(12.dp))

                // Stars selector with icons
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 1..5) {
                        IconButton(onClick = { stars = i }, modifier = Modifier.size(44.dp)) {
                            if (i <= stars) {
                                Icon(
                                        Icons.Filled.Star,
                                        contentDescription = "Star $i",
                                        tint = Color(0xFFD35400)
                                )
                            } else {
                                Icon(
                                        Icons.Filled.Star,
                                        contentDescription = "Star $i",
                                        tint = Color.LightGray
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Comentario") },
                        modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                        onClick = { onSubmit(stars, comment) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD35400))
                ) { Text("Enviar reseña", color = Color.White) }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RatingPreview() {
    PizzaHub_MobileTheme { RatingScreen(orderId = "o1", onBack = {}) }
}
