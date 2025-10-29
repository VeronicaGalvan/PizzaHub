package com.example.pizzahub_mobile.data.models

data class Product(
    val id: String,
    val name: String,
    val description: String? = null,
    val price: Double,
    val imageUrl: String? = null
)

