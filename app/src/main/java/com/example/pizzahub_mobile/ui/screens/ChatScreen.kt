package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ChatMessage(val id: String, val text: String, val isUser: Boolean)

@Composable
fun ChatScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val cream = Color(0xFFFFF8EE)
    val terracota = Color(0xFFD35400)
    val brownDark = Color(0xFF4E342E)
    val softBeige = Color(0xFFFFEEDD)
    var input by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val scope = rememberCoroutineScope()

    // mensaje inicial
    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages.add(
                    ChatMessage(
                            "m1",
                            "👋 ¡Hola! Soy tu asistente PizzaHub. ¿Deseas repetir un pedido o ver nuestras recomendaciones?",
                            false
                    )
            )
        }
    }

    Column(modifier = modifier.fillMaxSize().background(cream)) {
        // encabezado con título centrado
        Box(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = brownDark)
            }
            Text("Asistente", color = brownDark, fontWeight = FontWeight.Bold)
        }

        // mensajes
        LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                                if (msg.isUser) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                            shape = RoundedCornerShape(14.dp),
                            colors =
                                    CardDefaults.cardColors(
                                            containerColor =
                                                    if (msg.isUser) terracota else softBeige
                                    ),
                            modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                                msg.text,
                                modifier = Modifier.padding(12.dp),
                                color = if (msg.isUser) Color.White else brownDark,
                                fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // input
        Surface(tonalElevation = 2.dp, color = cream) {
            Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                        value = input,
                        onValueChange = { input = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Escribe un mensaje...") },
                        singleLine = true
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                        onClick = {
                            val text = input.trim()
                            if (text.isNotEmpty()) {
                                messages.add(ChatMessage("u${messages.size}", text, true))
                                input = ""
                                scope.launch {
                                    delay(700)
                                    messages.add(
                                            ChatMessage(
                                                    "b${messages.size}",
                                                    generateBotReply(text),
                                                    false
                                            )
                                    )
                                }
                            }
                        }
                ) { Icon(Icons.Filled.Send, contentDescription = "Enviar", tint = terracota) }
            }
        }
    }
}

private fun generateBotReply(text: String): String {
    val t = text.lowercase()
    return when {
        "pizza" in t ->
                "🍕 Te recomiendo nuestra Pizza Pepperoni Especial. ¿La agrego a tu carrito?"
        "pedido" in t -> "Puedo ayudarte a repetir tu último pedido. ¿Deseas hacerlo?"
        "bebida" in t -> "Tenemos Coca-Cola, Agua Mineral y Naranjada. ¿Cuál prefieres?"
        else ->
                "Puedo recomendarte nuestras pizzas más populares o ayudarte a buscar por categoría. ¿Qué prefieres?"
    }
}

@Preview(showBackground = true)
@Composable
fun ChatPreview() {
    PizzaHub_MobileTheme { ChatScreen(onBack = {}) }
}
