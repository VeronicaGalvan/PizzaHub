package com.example.pizzahub_mobile.data.sample

import com.example.pizzahub_mobile.data.models.Product

object SampleData {
    val pizzas =
            listOf(
                    Product("1", "Pizza Pepperoni", "Clásica con pepperoni", 120.0, null),
                    Product("2", "Pizza Hawaiana", "Jamón y piña", 130.0, null),
                    Product("3", "Pizza Vegetariana", "Pimientos y champiñones", 140.0, null)
            )

    val beverages =
            listOf(
                    Product("b1", "Refresco Cola", "Lata 350ml", 25.0, null),
                    Product("b2", "Agua Mineral", "Botella 500ml", 20.0, null)
            )

    val complements =
            listOf(
                    Product("c1", "Pan de Ajo", "Porción para compartir", 35.0, null),
                    Product("c2", "Ensalada César", "Porción individual", 45.0, null)
            )
}
