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
    var input by remember { mutableStateOf("") }
    val messages = remember { mutableStateListOf<ChatMessage>() }
    val scope = rememberCoroutineScope()

    // prepopulate friendly prompt
    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages.add(
                    ChatMessage(
                            "m1",
                            "Hola! 👋 ¿En qué puedo ayudarte hoy? Puedo sugerirte pizzas populares o ayudarte a repetir un pedido.",
                            false
                    )
            )
        }
    }

    Column(modifier = modifier.fillMaxSize().background(cream)) {
        // Simple top bar (local copy to avoid depending on other files)
        Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text(
                    text = "Asistente",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
            )
        }

        LazyColumn(
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                reverseLayout = false
        ) {
            items(messages, key = { it.id }) { msg ->
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                                if (msg.isUser) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                            shape = RoundedCornerShape(12.dp),
                            colors =
                                    CardDefaults.cardColors(
                                            containerColor =
                                                    if (msg.isUser) terracota else Color.White
                                    ),
                            modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                    text = msg.text,
                                    color = if (msg.isUser) Color.White else Color(0xFF4E342E),
                                    fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Input row
        Surface(tonalElevation = 2.dp) {
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
                                val id = "u${messages.size + 1}"
                                messages.add(ChatMessage(id, text, true))
                                input = ""

                                // simulated bot reply
                                scope.launch {
                                    delay(800)
                                    val reply = generateBotReply(text)
                                    messages.add(ChatMessage("b${messages.size + 1}", reply, false))
                                }
                            }
                        }
                ) { Icon(Icons.Filled.Send, contentDescription = "Enviar", tint = terracota) }
            }
        }
    }
}

// Simple canned reply generator (design-only). Replace with server/AI integration later.
private fun generateBotReply(userText: String): String {
    val low = userText.lowercase()
    return when {
        "pizza" in low ->
                "Te recomiendo nuestra pizza Pepperoni Especial. ¿Quieres agregarla al carrito? 🍕"
        "repetir" in low || "otra vez" in low -> "Puedo repetir tu último pedido. ¿Confirmas?"
        "bebida" in low || "refresco" in low -> "Tenemos Coca-Cola y Agua Mineral. ¿Cuál prefieres?"
        else ->
                "Interesante — puedo recomendarte nuestras pizzas más populares o ayudarte a buscar por categoría. ¿Qué prefieres?"
    }
}

@Preview(showBackground = true)
@Composable
fun ChatPreview() {
    PizzaHub_MobileTheme { ChatScreen(onBack = {}) }
}
