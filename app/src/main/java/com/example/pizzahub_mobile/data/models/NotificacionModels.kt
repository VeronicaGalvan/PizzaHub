package com.example.pizzahub_mobile.data.models

import com.squareup.moshi.Json

data class NotificacionDto(
        @Json(name = "id") val id: Int,
        @Json(name = "titulo") val titulo: String,
        @Json(name = "mensaje") val mensaje: String,
        @Json(name = "tipo") val tipo: String?,
        @Json(name = "pedidoId") val pedidoId: Int?,
        @Json(name = "leida") val leida: Boolean,
        @Json(name = "enviada") val enviada: Boolean,
        @Json(name = "fechaCreacion") val fechaCreacion: String?,
        @Json(name = "fechaLectura") val fechaLectura: String?
)

data class RegistrarTokenFCMDto(@Json(name = "fcmToken") val FcmToken: String)
