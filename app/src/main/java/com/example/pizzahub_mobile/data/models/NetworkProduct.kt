package com.example.pizzahub_mobile.data.models

import com.squareup.moshi.Json

data class ProductResponse(
        @Json(name = "id") val id: Int,
        @Json(name = "nombre") val nombre: String,
        @Json(name = "descripcion") val descripcion: String?,
        @Json(name = "precio") val precio: Double,
        @Json(name = "stockActual") val stockActual: Int? = null,
        @Json(name = "stockMinimo") val stockMinimo: Int? = null,
        @Json(name = "rutaImagen") val rutaImagen: String? = null,
        @Json(name = "activo") val activo: Boolean = true
)

fun ProductResponse.toProduct(): Product =
        Product(
                id = id.toString(),
                name = nombre,
                description = descripcion,
                price = precio,
                imageUrl = rutaImagen
        )
