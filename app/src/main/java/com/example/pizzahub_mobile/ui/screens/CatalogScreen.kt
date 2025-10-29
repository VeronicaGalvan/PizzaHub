package com.example.pizzahub_mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.pizzahub_mobile.data.sample.SampleData
import com.example.pizzahub_mobile.ui.components.ProductCard
import com.example.pizzahub_mobile.ui.theme.PizzaBrown
import com.example.pizzahub_mobile.ui.theme.PizzaHub_MobileTheme

@Composable
fun CatalogScreen(onBack: () -> Unit, onNavigate: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
            }

            Text("Catálogo", style = MaterialTheme.typography.titleMedium, color = PizzaBrown)

            IconButton(onClick = { onNavigate("order") }) {
                Icon(imageVector = Icons.Filled.ShoppingCart, contentDescription = "Cart")
            }
        }

        LazyColumn {
            itemsIndexed(SampleData.pizzas) { index, pizza ->
                ProductCard(product = pizza, isHighlighted = index == 0)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CatalogScreenPreview() {
    PizzaHub_MobileTheme { CatalogScreen(onBack = {}, onNavigate = {}) }
}
