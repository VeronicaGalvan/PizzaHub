package com.example.pizzahub_mobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzahub_mobile.data.models.Product
import com.example.pizzahub_mobile.data.network.ProductsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ProductsViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = ProductsRepository(application.applicationContext)

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun loadProducts() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val r = repo.fetchProducts()
            if (r.isSuccess) {
                _products.value = r.getOrNull() ?: emptyList()
            } else {
                _error.value = r.exceptionOrNull()?.localizedMessage ?: "Error al cargar productos"
            }
            _isLoading.value = false
        }
    }
}
