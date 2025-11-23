package com.example.pizzahub_mobile.data.models

import com.squareup.moshi.Json

/** Request para crear una calificación POST /api/Calificaciones/pedido/{pedidoId} */
data class CalificacionRequest(
        @Json(name = "estrellas") val estrellas: Int,
        @Json(name = "comentario") val comentario: String
)
