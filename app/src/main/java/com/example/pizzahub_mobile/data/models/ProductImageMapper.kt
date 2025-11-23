package com.example.pizzahub_mobile.data.models

import com.example.pizzahub_mobile.R

/** Mapper para convertir identificadores de imágenes a recursos drawable */
object ProductImageMapper {
    fun getDrawableResource(imagenUrl: String?): Int {
        if (imagenUrl.isNullOrBlank()) {
            return R.drawable.pizza_hawaiana
        }

        // Extraer el nombre del archivo de una URL si viene como URL completa
        val fileName = imagenUrl.substringAfterLast("/").substringBeforeLast(".").lowercase()

        // Intentar hacer match con el nombre completo o con el nombre del archivo extraído
        return when {
            // Pizzas - buscar por nombre de archivo
            imagenUrl.contains("hawaiana", ignoreCase = true) || fileName.contains("hawaiana") ->
                    R.drawable.pizza_hawaiana
            imagenUrl.contains("pepperoni", ignoreCase = true) || fileName.contains("pepperoni") ->
                    R.drawable.pizza_pepperoni
            imagenUrl.contains("vegetariana", ignoreCase = true) ||
                    fileName.contains("vegetariana") -> R.drawable.pizza_vegetariana
            imagenUrl.contains("margarita", ignoreCase = true) || fileName.contains("margarita") ->
                    R.drawable.pizza_margarita
            imagenUrl.contains("mexicana", ignoreCase = true) || fileName.contains("mexicana") ->
                    R.drawable.pizza_mexicana
            imagenUrl.contains("4quesos", ignoreCase = true) ||
                    imagenUrl.contains("cuatro", ignoreCase = true) ||
                    fileName.contains("4quesos") ||
                    fileName.contains("quesos") -> R.drawable.pizza_4quesos
            imagenUrl.contains("bbq", ignoreCase = true) || fileName.contains("bbq") ->
                    R.drawable.pizza_bbq
            imagenUrl.contains("carnesfrias", ignoreCase = true) ||
                    imagenUrl.contains("carnes", ignoreCase = true) ||
                    fileName.contains("carnes") -> R.drawable.pizza_carnesfrias

            // Bebidas
            imagenUrl.contains("cola", ignoreCase = true) || fileName.contains("cola") ->
                    R.drawable.refresco_cola
            imagenUrl.contains("sprite", ignoreCase = true) || fileName.contains("sprite") ->
                    R.drawable.refresco_sprite

            // Complementos
            imagenUrl.contains("papas", ignoreCase = true) || fileName.contains("papas") ->
                    R.drawable.complemento_papas
            imagenUrl.contains("aros", ignoreCase = true) || fileName.contains("aros") ->
                    R.drawable.complemento_aros

            // Default fallback
            else -> R.drawable.pizza_hawaiana
        }
    }

    /** Versión que también toma en cuenta el nombre del producto para mayor precisión */
    fun getDrawableResourceByProductName(productName: String?, imagenUrl: String?): Int {
        val combinedText = "${productName?.lowercase() ?: ""} ${imagenUrl?.lowercase() ?: ""}"

        return when {
            // Pizzas
            combinedText.contains("hawaiana") -> R.drawable.pizza_hawaiana
            combinedText.contains("pepperoni") -> R.drawable.pizza_pepperoni
            combinedText.contains("vegetariana") -> R.drawable.pizza_vegetariana
            combinedText.contains("margarita") -> R.drawable.pizza_margarita
            combinedText.contains("mexicana") -> R.drawable.pizza_mexicana
            combinedText.contains("4 quesos") ||
                    combinedText.contains("cuatro quesos") ||
                    combinedText.contains("4quesos") -> R.drawable.pizza_4quesos
            combinedText.contains("bbq") -> R.drawable.pizza_bbq
            combinedText.contains("carnes frias") || combinedText.contains("carnes frías") ->
                    R.drawable.pizza_carnesfrias

            // Bebidas (orden importante: sprite primero para evitar conflictos)
            combinedText.contains("sprite") -> R.drawable.refresco_sprite
            combinedText.contains("cola") ||
                    combinedText.contains("coca") ||
                    combinedText.contains("refresco") ||
                    combinedText.contains("soda") ||
                    combinedText.contains("gaseosa") -> R.drawable.refresco_cola

            // Complementos
            combinedText.contains("papas") || combinedText.contains("papa") ->
                    R.drawable.complemento_papas
            combinedText.contains("aros") || combinedText.contains("cebolla") ->
                    R.drawable.complemento_aros

            // Default
            else -> R.drawable.pizza_hawaiana
        }
    }
}
