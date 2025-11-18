package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme
import com.example.pizzahub_mobile.ui.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
        onBack: () -> Unit,
        modifier: Modifier = Modifier,
        chatViewModel: ChatViewModel = viewModel()
) {
    val cream = Color(0xFFFFF8EE)
    val terracota = Color(0xFFD35400)
    val brownDark = Color(0xFF4E342E)
    val softBeige = Color(0xFFFFEEDD)

    var input by remember { mutableStateOf("") }
    val messages by chatViewModel.messages.collectAsState()
    val isLoading by chatViewModel.isLoading.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    // Auto-scroll al último mensaje
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Column(modifier = modifier.fillMaxSize().background(cream)) {
        // Encabezado con título centrado
        Box(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterStart)) {
                Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = brownDark)
            }
            Text("🍕 PizzaBot", color = brownDark, fontWeight = FontWeight.Bold, fontSize = 18.sp)

            IconButton(
                    onClick = { chatViewModel.clearChat() },
                    modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = "Reiniciar chat", tint = brownDark)
            }
        }

        // Mensajes
        LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(messages, key = { it.timestamp }) { msg ->
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement =
                                if (msg.isFromUser) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                            shape = RoundedCornerShape(14.dp),
                            colors =
                                    CardDefaults.cardColors(
                                            containerColor =
                                                    if (msg.isFromUser) terracota else softBeige
                                    ),
                            modifier = Modifier.widthIn(max = 280.dp)
                    ) {
                        Text(
                                msg.text,
                                modifier = Modifier.padding(12.dp),
                                color = if (msg.isFromUser) Color.White else brownDark,
                                fontSize = 14.sp
                        )
                    }
                }
            }

            // Indicador de escritura
            if (isLoading) {
                item {
                    Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start
                    ) {
                        Card(
                                shape = RoundedCornerShape(14.dp),
                                colors = CardDefaults.cardColors(containerColor = softBeige)
                        ) {
                            Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        strokeWidth = 2.dp,
                                        color = terracota
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                        "PizzaBot está escribiendo...",
                                        color = brownDark.copy(alpha = 0.7f),
                                        fontSize = 12.sp,
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }
                }
            }
        }

        // Input
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
                        singleLine = true,
                        enabled = !isLoading
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                        onClick = {
                            val text = input.trim()
                            if (text.isNotEmpty() && !isLoading) {
                                chatViewModel.sendMessage(text)
                                input = ""
                            }
                        },
                        enabled = input.trim().isNotEmpty() && !isLoading
                ) {
                    Icon(
                            Icons.Filled.Send,
                            contentDescription = "Enviar",
                            tint =
                                    if (input.trim().isNotEmpty() && !isLoading) terracota
                                    else Color.Gray
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ChatPreview() {
    PizzaHub_MobileTheme { ChatScreen(onBack = {}) }
}
