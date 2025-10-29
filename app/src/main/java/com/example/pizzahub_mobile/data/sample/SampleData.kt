package com.example.pizzahub_mobile.data.sample

import com.example.pizzahub_mobile.data.models.Product

object SampleData {
    val pizzas = listOf(
        Product("1", "Pizza Pepperoni", "Clásica con pepperoni", 120.0, null),
        Product("2", "Pizza Hawaiana", "Jamón y piña", 130.0, null),
        Product("3", "Pizza Vegetariana", "Pimientos y champiñones", 140.0, null)
    )
}

