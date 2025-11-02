package com.example.pizzahub_mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.example.pizzahub_mobile.data.models.Product
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

data class CartItem(val product: Product, val quantity: Int = 1)

data class CartState(val items: List<CartItem> = emptyList())

class CartViewModel : ViewModel() {
    private val _state = MutableStateFlow(CartState())
    val cartState: StateFlow<CartState> = _state

    fun addProduct(product: Product) {
        val current = _state.value.items.toMutableList()
        val idx = current.indexOfFirst { it.product.id == product.id }
        if (idx >= 0) {
            val existing = current[idx]
            current[idx] = existing.copy(quantity = existing.quantity + 1)
        } else {
            current.add(CartItem(product, 1))
        }
        _state.value = CartState(items = current)
    }

    fun updateQuantity(productId: String, quantity: Int) {
        if (quantity < 1) return
        val current =
                _state.value.items.map {
                    if (it.product.id == productId) it.copy(quantity = quantity) else it
                }
        _state.value = CartState(items = current)
    }

    fun removeItem(productId: String) {
        val current = _state.value.items.filter { it.product.id != productId }
        _state.value = CartState(items = current)
    }

    fun clearCart() {
        _state.value = CartState()
    }

    fun subtotal(): Double = _state.value.items.sumOf { it.product.price * it.quantity }
}
