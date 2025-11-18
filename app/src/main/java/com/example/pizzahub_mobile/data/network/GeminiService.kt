package com.example.pizzahub_mobile.data.network

import android.util.Log
import com.example.pizzahub_mobile.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query

// ======================== DATA CLASSES ========================

@JsonClass(generateAdapter = true)
data class GeminiRequest(
        @Json(name = "contents") val contents: List<Content>,
        @Json(name = "systemInstruction") val systemInstruction: SystemInstruction? = null,
        @Json(name = "generationConfig") val generationConfig: GenerationConfig? = null
)

@JsonClass(generateAdapter = true)
data class SystemInstruction(@Json(name = "parts") val parts: List<Part>)

@JsonClass(generateAdapter = true)
data class Content(
        @Json(name = "parts") val parts: List<Part>,
        @Json(name = "role") val role: String? = null
)

@JsonClass(generateAdapter = true) data class Part(@Json(name = "text") val text: String)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
        @Json(name = "temperature") val temperature: Double = 0.9,
        @Json(name = "topK") val topK: Int = 40,
        @Json(name = "topP") val topP: Double = 0.95,
        @Json(name = "maxOutputTokens") val maxOutputTokens: Int = 2048
)

@JsonClass(generateAdapter = true)
data class GeminiResponse(
        @Json(name = "candidates") val candidates: List<Candidate>? = null,
        @Json(name = "error") val error: GeminiError? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(
        @Json(name = "content") val content: Content,
        @Json(name = "finishReason") val finishReason: String? = null
)

@JsonClass(generateAdapter = true)
data class GeminiError(
        @Json(name = "code") val code: Int,
        @Json(name = "message") val message: String,
        @Json(name = "status") val status: String
)

// ======================== RETROFIT API ========================

interface GeminiApi {
        @POST("v1beta/models/gemini-2.5-flash:generateContent")
        suspend fun generateContent(
                @Query("key") apiKey: String,
                @Body request: GeminiRequest
        ): GeminiResponse
}

// ======================== SERVICE CLASS ========================

class GeminiService {
        companion object {
                // ⚠️ NUNCA expongas API keys directamente
                // Usa BuildConfig para mantener las credenciales seguras
                private val API_KEY = BuildConfig.GEMINI_API_KEY
                private const val BASE_URL = "https://generativelanguage.googleapis.com/"
        }

        private val systemInstructionText =
                """
Eres 'PizzaBot', un asistente amigable y experto en pizzas de PizzaHub 🍕. 
Tu única función es ayudar a los usuarios a encontrar la pizza perfecta según sus preferencias.

INSTRUCCIONES IMPORTANTES:
1. SOLO habla sobre pizzas de PizzaHub. Si te preguntan sobre otros temas, amablemente redirige la conversación a las pizzas.
2. Haz UNA pregunta a la vez para entender las preferencias del cliente.
3. Pregunta sobre:
   - Ingredientes preferidos o que NO les gustan
   - Nivel de picante (suave, medio, picante)
   - Restricciones dietéticas (vegetariano, sin cerdo, etc.)
4. Después de entender sus preferencias, recomienda 2-3 pizzas específicas del menú.
5. Sé conciso pero amigable. Usa emojis ocasionalmente 🍕.

MENÚ DE PIZZAS DISPONIBLES:
🍕 Hawaiana - Jamón, piña, queso mozzarella
🍕 Pepperoni - Pepperoni, queso mozzarella, salsa de tomate
🍕 Vegetariana - Pimientos, champiñones, aceitunas, cebolla, tomate
🍕 Margarita - Tomate fresco, albahaca, queso mozzarella
🍕 Mexicana - Carne molida, jalapeños, cebolla, pimientos, queso cheddar
🍕 Cuatro Quesos - Mozzarella, parmesano, gorgonzola, provolone
🍕 BBQ Chicken - Pollo, salsa BBQ, cebolla, tocino
🍕 Carnes Frías - Jamón, salami, pepperoni, chorizo

Responde en español y siguiendo estas instrucciones estrictamente.
    """.trimIndent()

        private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        private val retrofit =
                Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(MoshiConverterFactory.create(moshi))
                        .build()

        private val api = retrofit.create(GeminiApi::class.java)

        // Conversation history for multi-turn chat
        private val conversationHistory = mutableListOf<Content>()

        suspend fun sendMessage(userMessage: String): Flow<String> = flow {
                try {
                        Log.d("GeminiService", "📤 Enviando mensaje: $userMessage")

                        // Add user message to conversation history
                        conversationHistory.add(
                                Content(parts = listOf(Part(text = userMessage)), role = "user")
                        )

                        // Create request with system instruction and conversation history
                        val request =
                                GeminiRequest(
                                        systemInstruction =
                                                SystemInstruction(
                                                        parts =
                                                                listOf(
                                                                        Part(
                                                                                text =
                                                                                        systemInstructionText
                                                                        )
                                                                )
                                                ),
                                        contents = conversationHistory,
                                        generationConfig =
                                                GenerationConfig(
                                                        temperature = 0.9,
                                                        topK = 40,
                                                        topP = 0.95,
                                                        maxOutputTokens = 2048
                                                )
                                )

                        // Call Gemini API
                        val response = api.generateContent(API_KEY, request)

                        // Check for errors
                        if (response.error != null) {
                                val errorMsg =
                                        "❌ Error ${response.error.code}: ${response.error.message}"
                                Log.e("GeminiService", errorMsg)
                                emit(errorMsg)
                                return@flow
                        }

                        // Extract response text
                        val responseText =
                                response.candidates
                                        ?.firstOrNull()
                                        ?.content
                                        ?.parts
                                        ?.firstOrNull()
                                        ?.text
                                        ?: "Lo siento, no pude generar una respuesta."

                        Log.d("GeminiService", "✅ Respuesta recibida: ${responseText.take(100)}...")

                        // Add bot response to conversation history
                        conversationHistory.add(
                                Content(parts = listOf(Part(text = responseText)), role = "model")
                        )

                        emit(responseText)
                } catch (e: Exception) {
                        Log.e("GeminiService", "❌ Error al llamar a Gemini API", e)
                        val errorMessage =
                                when {
                                        e.message?.contains("quota", ignoreCase = true) == true ->
                                                "⏱️ He recibido muchas preguntas. Por favor espera un momento e intenta de nuevo."
                                        e.message?.contains("authentication", ignoreCase = true) ==
                                                true ||
                                                e.message?.contains("api key", ignoreCase = true) ==
                                                        true ->
                                                "🔑 Error de autenticación. Verifica la API key."
                                        e.message?.contains("network", ignoreCase = true) == true ||
                                                e.message?.contains(
                                                        "Unable to resolve host",
                                                        ignoreCase = true
                                                ) == true ->
                                                "🌐 Sin conexión a internet. Verifica tu red."
                                        e.message?.contains("timeout", ignoreCase = true) == true ->
                                                "⏱️ Tiempo de espera agotado. Intenta de nuevo."
                                        else ->
                                                "❌ Error: ${e.message ?: "No se pudo conectar con el asistente"}"
                                }
                        Log.e("GeminiService", "Error details: $errorMessage")
                        emit(errorMessage)
                }
        }

        fun resetChat() {
                Log.d("GeminiService", "🔄 Reiniciando chat")
                conversationHistory.clear()
        }
}
