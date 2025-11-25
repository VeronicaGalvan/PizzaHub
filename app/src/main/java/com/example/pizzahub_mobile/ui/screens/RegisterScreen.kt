package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
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
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme

@Composable
fun RegisterScreen(
        onBack: (() -> Unit)? = null,
        // onRegister will receive the access token on success
        onRegister: (String) -> Unit,
        onNavigateToLogin: () -> Unit = {},
        authViewModel: com.example.pizzahub_mobile.ui.viewmodel.AuthViewModel? = null
) {
        val terracota = Color(0xFFC0392B)
        val cream = Color(0xFFFFF4E8)
        val brownDark = Color(0xFF4E342E)

        var nombreCompleto by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var telefonoContacto by remember { mutableStateOf("") }

        val scope = rememberCoroutineScope()
        // ViewModel and UI state (use provided or get local one)
        val localAuthViewModel =
                authViewModel ?: viewModel<com.example.pizzahub_mobile.ui.viewmodel.AuthViewModel>()
        val loading by localAuthViewModel.isLoading.collectAsState()
        val error by localAuthViewModel.error.collectAsState()
        val isAuthenticated by localAuthViewModel.isAuthenticated.collectAsState()
        var localError by remember { mutableStateOf<String?>(null) }
        Surface(modifier = Modifier.fillMaxSize().background(cream), color = cream) {
                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .padding(24.dp)
                                        .verticalScroll(rememberScrollState()),
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
                        // Imagen central (emoji)
                        Box(
                                modifier = Modifier
                                        .size(110.dp)
                                        .shadow(8.dp, shape = CircleShape, clip = false)
                                        .background(terracota, shape = CircleShape),
                                contentAlignment = Alignment.Center
                        ) {
                                Image(
                                        painter = painterResource(id = R.drawable.logo2),
                                        contentDescription = "Logo PizzaHub",
                                        modifier = Modifier
                                                .size(105.dp)
                                                .clip(CircleShape),     // 🔥 Recorta la imagen dentro del círculo
                                        contentScale = ContentScale.Crop
                                )
                        }

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
                                text = "Crea tu cuenta para comenzar a disfrutar nuestras pizzas",
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
                                                value = nombreCompleto,
                                                onValueChange = { newValue ->
                                                        // Solo letras, espacios y acentos
                                                        if (newValue.all {
                                                                        it.isLetter() ||
                                                                                it.isWhitespace()
                                                                }
                                                        ) {
                                                                nombreCompleto = newValue
                                                        }
                                                },
                                                label = { Text("Nombre completo") },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Text
                                                        )
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

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

                                        Spacer(modifier = Modifier.height(12.dp))

                                        OutlinedTextField(
                                                value = password,
                                                onValueChange = { password = it },
                                                label = {
                                                        Text("Contraseña (mínimo 8 caracteres)")
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                visualTransformation =
                                                        PasswordVisualTransformation()
                                        )

                                        Spacer(modifier = Modifier.height(12.dp))

                                        OutlinedTextField(
                                                value = telefonoContacto,
                                                onValueChange = { newValue ->
                                                        // Solo números y máximo 10 dígitos
                                                        if (newValue.all { it.isDigit() } &&
                                                                        newValue.length <= 10
                                                        ) {
                                                                telefonoContacto = newValue
                                                        }
                                                },
                                                label = {
                                                        Text("Teléfono de contacto (10 dígitos)")
                                                },
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                keyboardOptions =
                                                        KeyboardOptions(
                                                                keyboardType = KeyboardType.Phone
                                                        )
                                        )

                                        Spacer(modifier = Modifier.height(24.dp))

                                        Button(
                                                onClick = {
                                                        // Basic client-side validation
                                                        localError = null

                                                        if (nombreCompleto.isBlank()) {
                                                                localError =
                                                                        "Ingrese su nombre completo"
                                                                return@Button
                                                        }

                                                        if (!android.util.Patterns.EMAIL_ADDRESS
                                                                        .matcher(email)
                                                                        .matches()
                                                        ) {
                                                                localError =
                                                                        "Ingrese un correo electrónico válido"
                                                                return@Button
                                                        }
                                                        if (password.length < 8) {
                                                                localError =
                                                                        "La contraseña debe tener al menos 8 caracteres"
                                                                return@Button
                                                        }

                                                        if (telefonoContacto.length != 10) {
                                                                localError =
                                                                        "El teléfono debe tener exactamente 10 dígitos"
                                                                return@Button
                                                        }
                                                        if (nombreCompleto.isBlank()) {
                                                                localError =
                                                                        "El nombre completo es requerido"
                                                                return@Button
                                                        }
                                                        if (telefonoContacto.isBlank()) {
                                                                localError =
                                                                        "El teléfono de contacto es requerido"
                                                                return@Button
                                                        }

                                                        localAuthViewModel.register(
                                                                email,
                                                                password,
                                                                nombreCompleto,
                                                                telefonoContacto
                                                        )
                                                },
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
                                                                text = "Registrarme",
                                                                color = Color.White,
                                                                fontWeight = FontWeight.SemiBold,
                                                                fontSize = 16.sp
                                                        )
                                        }
                                }
                        }

                        localError?.let {
                                Text(
                                        text = it,
                                        color = Color.Red,
                                        modifier = Modifier.padding(top = 8.dp)
                                )
                        }

                        error?.let {
                                Text(
                                        text = it,
                                        color = Color.Red,
                                        modifier = Modifier.padding(top = 8.dp)
                                )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Si el registro fue exitoso, notificar al caller para navegar
                        LaunchedEffect(isAuthenticated) {
                                if (isAuthenticated) {
                                        // onRegister receives an access token in the NavHost
                                        // signature,
                                        // but the NavHost ignores it; pass empty for compatibility.
                                        onRegister("")
                                }
                        }

                        // 🔗 Enlace de volver al inicio de sesión
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                        ) {
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
        PizzaHub_MobileTheme { RegisterScreen(onRegister = { _ -> }) }
}
