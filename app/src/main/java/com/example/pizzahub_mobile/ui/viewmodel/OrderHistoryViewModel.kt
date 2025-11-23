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

class OrderHistoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(RetrofitInstance.authApi, getApplication())

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _pedidos = MutableStateFlow<List<PedidoResponse>>(emptyList())
    val pedidos: StateFlow<List<PedidoResponse>> = _pedidos.asStateFlow()

    private val _repetirPedidoSuccess = MutableStateFlow<PedidoResponse?>(null)
    val repetirPedidoSuccess: StateFlow<PedidoResponse?> = _repetirPedidoSuccess.asStateFlow()

    fun loadPedidos(clienteId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            repository
                    .getPedidosByCliente(clienteId)
                    .fold(
                            onSuccess = { pedidosList ->
                                _pedidos.value = pedidosList
                                Log.d(
                                        "OrderHistoryViewModel",
                                        "Loaded ${pedidosList.size} pedidos for cliente $clienteId"
                                )
                            },
                            onFailure = { e ->
                                _error.value =
                                        e.message ?: "Error al cargar el historial de pedidos"
                                Log.e("OrderHistoryViewModel", "Error loading pedidos", e)
                            }
                    )

            _isLoading.value = false
        }
    }

    fun repetirPedido(pedidoId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _repetirPedidoSuccess.value = null

            repository
                    .repetirPedido(pedidoId)
                    .fold(
                            onSuccess = { nuevoPedido ->
                                _repetirPedidoSuccess.value = nuevoPedido
                                Log.d(
                                        "OrderHistoryViewModel",
                                        "Pedido repetido exitosamente: ${nuevoPedido.id}"
                                )
                            },
                            onFailure = { e ->
                                _error.value = e.message ?: "Error al repetir el pedido"
                                Log.e("OrderHistoryViewModel", "Error repitiendo pedido", e)
                            }
                    )

            _isLoading.value = false
        }
    }

    fun clearRepetirPedidoSuccess() {
        _repetirPedidoSuccess.value = null
    }

    fun clearError() {
        _error.value = null
    }
}
