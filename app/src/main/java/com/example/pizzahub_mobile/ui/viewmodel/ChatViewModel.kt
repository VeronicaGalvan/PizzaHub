package com.example.pizzahub_mobile.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzahub_mobile.data.network.GeminiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ChatMessage(
        val text: String,
        val isFromUser: Boolean,
        val timestamp: Long = System.currentTimeMillis()
)

class ChatViewModel : ViewModel() {
        private val geminiService = GeminiService()

        private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
        val messages: StateFlow<List<ChatMessage>> = _messages.asStateFlow()

        private val _isLoading = MutableStateFlow(false)
        val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

        init {
                // Mensaje de bienvenida del bot
                addMessage(
                        ChatMessage(
                                text =
                                        "¡Hola! 👋 Soy PizzaBot, tu asistente personal de PizzaHub 🍕\n\n¿En qué te puedo ayudar hoy? ¿Quieres que te ayude a elegir la pizza perfecta para ti?",
                                isFromUser = false
                        )
                )
        }

        fun sendMessage(userMessage: String) {
                if (userMessage.isBlank()) return

                Log.d("ChatViewModel", "Enviando mensaje del usuario: $userMessage")

                // Agregar mensaje del usuario
                addMessage(ChatMessage(text = userMessage, isFromUser = true))

                // Enviar a Gemini y obtener respuesta
                viewModelScope.launch {
                        _isLoading.value = true
                        Log.d("ChatViewModel", "Iniciando llamada a GeminiService...")
                        try {
                                geminiService.sendMessage(userMessage).collect { response ->
                                        Log.d(
                                                "ChatViewModel",
                                                "Respuesta recibida del servicio: ${response.take(50)}..."
                                        )
                                        addMessage(ChatMessage(text = response, isFromUser = false))
                                }
                        } catch (e: Exception) {
                                Log.e("ChatViewModel", "Error en sendMessage", e)
                                addMessage(
                                        ChatMessage(
                                                text =
                                                        "Lo siento, hubo un error al procesar tu mensaje. Por favor intenta de nuevo.",
                                                isFromUser = false
                                        )
                                )
                        } finally {
                                _isLoading.value = false
                                Log.d("ChatViewModel", "Finalizó el procesamiento del mensaje")
                        }
                }
        }

        private fun addMessage(message: ChatMessage) {
                _messages.value = _messages.value + message
        }

        fun clearChat() {
                _messages.value =
                        listOf(
                                ChatMessage(
                                        text =
                                                "¡Hola! 👋 Soy PizzaBot, tu asistente personal de PizzaHub 🍕\n\n¿En qué te puedo ayudar hoy? ¿Quieres que te ayude a elegir la pizza perfecta para ti?",
                                        isFromUser = false
                                )
                        )
                geminiService.resetChat()
        }
}
