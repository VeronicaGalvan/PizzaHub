package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme

@Composable
fun RegisterScreen(
        onBack: (() -> Unit)? = null,
        onRegister: (String, String) -> Unit,
        onNavigateToLogin: () -> Unit = {}
) {
    val terracota = Color(0xFFC0392B)
    val cream = Color(0xFFFFF4E8)
    val brownDark = Color(0xFF4E342E)

    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    Surface(modifier = Modifier.fillMaxSize().background(cream), color = cream) {
        Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // 🔙 Botón de regreso opcional
            if (onBack != null) {
                Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Volver",
                                tint = brownDark
                        )
                    }
                }
            }

            // 🍕 Ilustración superior
            Box(
                    modifier =
                            Modifier.size(110.dp)
                                    .clip(CircleShape)
                                    .background(terracota.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
            ) { Text(text = "👨‍🍳", fontSize = 50.sp) }

            Spacer(modifier = Modifier.height(16.dp))

            // 🧾 Encabezado
            Text(
                    text = "Crea tu cuenta",
                    color = brownDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                    text = "Regístrate con tu nombre y número de teléfono para comenzar",
                    color = brownDark.copy(alpha = 0.8f),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 🪶 Tarjeta del formulario
            Card(
                    modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Nombre completo") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Número de teléfono") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                            onClick = { onRegister(name, phone) },
                            modifier = Modifier.fillMaxWidth().height(52.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = terracota),
                            shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                                text = "Registrarme",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 🔗 Enlace de volver al inicio de sesión
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                Text(
                        text = "¿Ya tienes cuenta?",
                        color = brownDark.copy(alpha = 0.75f),
                        fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                        text = "Inicia sesión",
                        color = terracota,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                    text = "¡Listo para disfrutar tu pizza favorita! 🍕",
                    color = brownDark.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    PizzaHub_MobileTheme { RegisterScreen(onRegister = { _, _ -> }) }
}
