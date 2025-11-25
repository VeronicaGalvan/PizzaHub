package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzahub_mobile.R
import com.example.pizzahub_mobile.data.storage.TokenDataStore
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme
import com.example.pizzahub_mobile.ui.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
        onBack: () -> Unit,
        onLogin: (String) -> Unit,
        onNavigateToRegister: () -> Unit = {}
) {
        // Paleta consistente
        val terracota = Color(0xFFC0392B)
        val cream = Color(0xFFFFF4E8)
        val brownDark = Color(0xFF4E342E)
        val softBeige = Color(0xFFFFEEDD)

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        val scope = rememberCoroutineScope()
        val authViewModel: AuthViewModel = viewModel()
        val loading by authViewModel.isLoading.collectAsState()
        val error by authViewModel.error.collectAsState()
        val isAuthenticated by authViewModel.isAuthenticated.collectAsState()
        val ctx = LocalContext.current

        LaunchedEffect(Unit) { authViewModel.checkExistingToken() }
        LaunchedEffect(isAuthenticated) {
                if (isAuthenticated) {
                        // notify caller with the stored access token
                        onLogin(TokenDataStore.getAccessTokenBlocking(ctx) ?: "")
                }
        }

        Surface(modifier = Modifier.fillMaxSize().background(cream), color = cream) {
                Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {

                        // 🔙 Botón superior para volver
                        Row(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
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

                        // 🍕 Ilustración central
                        // Imagen central (emoji)
                        Box(
                                modifier = Modifier
                                        .size(120.dp)
                                        .shadow(8.dp, shape = CircleShape, clip = false)
                                        .background(terracota, shape = CircleShape),
                                contentAlignment = Alignment.Center
                        ) {
                                Image(
                                        painter = painterResource(id = R.drawable.logo2),
                                        contentDescription = "Logo PizzaHub",
                                        modifier = Modifier
                                                .size(115.dp)
                                                .clip(CircleShape),     // 🔥 Recorta la imagen dentro del círculo
                                        contentScale = ContentScale.Crop
                                )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // 🧾 Texto principal
                        Text(
                                text = "¡Bienvenido de nuevo!",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = brownDark,
                                textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                                text = "Inicia sesión para continuar con tu pedido",
                                color = brownDark.copy(alpha = 0.8f),
                                fontSize = 15.sp,
                                textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(28.dp))

                        // 🪶 Tarjeta con campos
                        Card(
                                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(20.dp)),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                        OutlinedTextField(
                                                value = email,
                                                onValueChange = { email = it.trim() },
                                                label = { Text("Correo electrónico") },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Email
                                                        )
                                        )

                                        Spacer(modifier = Modifier.height(16.dp))

                                        OutlinedTextField(
                                                value = password,
                                                onValueChange = { password = it },
                                                label = { Text("Contraseña") },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                visualTransformation =
                                                        PasswordVisualTransformation()
                                        )

                                        Spacer(modifier = Modifier.height(24.dp))

                                        Button(
                                                onClick = { authViewModel.login(email, password) },
                                                modifier = Modifier.fillMaxWidth().height(52.dp),
                                                colors =
                                                        ButtonDefaults.buttonColors(
                                                                containerColor = terracota
                                                        ),
                                                shape = RoundedCornerShape(14.dp)
                                        ) {
                                                if (loading) Text("…", color = Color.White)
                                                else
                                                        Text(
                                                                text = "Continuar",
                                                                color = Color.White,
                                                                fontWeight = FontWeight.SemiBold,
                                                                fontSize = 16.sp
                                                        )
                                        }
                                }
                        }
                        error?.let {
                                Text(
                                        text = it,
                                        color = Color.Red,
                                        modifier = Modifier.padding(top = 8.dp)
                                )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // 🔗 Opción de registro
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                        ) {
                                Text(
                                        text = "¿No tienes cuenta?",
                                        color = brownDark.copy(alpha = 0.75f),
                                        fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                        text = "Regístrate",
                                        color = terracota,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        modifier = Modifier.clickable { onNavigateToRegister() }
                                )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                                text = "La mejor pizza está a un paso 🍕",
                                color = brownDark.copy(alpha = 0.6f),
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center
                        )
                }
        }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
        PizzaHub_MobileTheme { LoginScreen(onBack = {}, onLogin = {}) }
}
