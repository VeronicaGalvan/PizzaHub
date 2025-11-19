package com.example.pizzahub_mobile.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzahub_mobile.data.models.PedidoResponse
import com.example.pizzahub_mobile.data.network.AuthRepository
import com.example.pizzahub_mobile.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderTrackingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(RetrofitInstance.authApi, getApplication())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _pedido = MutableStateFlow<PedidoResponse?>(null)
    val pedido: StateFlow<PedidoResponse?> = _pedido.asStateFlow()

    fun loadPedido(pedidoId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository
                    .getPedidoById(pedidoId)
                    .fold(
                            onSuccess = { pedidoResponse ->
                                _pedido.value = pedidoResponse
                                Log.d(
                                        "OrderTrackingViewModel",
                                        "Pedido loaded: ID=${pedidoResponse.id}, Estado=${pedidoResponse.estado}"
                                )
                            },
                            onFailure = { e ->
                                _error.value = e.message ?: "Error al cargar el pedido"
                                Log.e("OrderTrackingViewModel", "Error loading pedido", e)
                            }
                    )

            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
