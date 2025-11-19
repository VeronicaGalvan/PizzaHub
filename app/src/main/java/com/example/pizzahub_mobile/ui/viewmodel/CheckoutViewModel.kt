package com.example.pizzahub_mobile.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzahub_mobile.data.models.PedidoResponse
import com.example.pizzahub_mobile.data.network.AuthApi
import com.example.pizzahub_mobile.data.network.AuthRepository
import com.example.pizzahub_mobile.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/** ViewModel para manejar el checkout y creación de pedidos */
class CheckoutViewModel(application: Application) : AndroidViewModel(application) {
    private val ctx = application.applicationContext
    private val api: AuthApi = RetrofitInstance.create(ctx).create(AuthApi::class.java)
    private val repo = AuthRepository(api, ctx)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _pedidoCreated = MutableStateFlow<PedidoResponse?>(null)
    val pedidoCreated: StateFlow<PedidoResponse?> = _pedidoCreated

    /**
     * Crea un pedido a partir del carrito
     *
     * @param clienteId ID del cliente (obtenido del perfil)
     * @param tipo Tipo de pedido (1=Local, 2=ParaLlevar, 3=Express, 4=Domicilio)
     * @param metodoPago Método de pago (1=Efectivo, 2=Tarjeta, 3=Transferencia)
     * @param direccionEntrega Dirección de entrega (requerido si tipo=4 Domicilio)
     * @param observaciones Observaciones del pedido (opcional)
     * @param cartItems Items del carrito a incluir en el pedido
     */
    fun createPedido(
            clienteId: Int,
            tipo: Int,
            metodoPago: Int,
            direccionEntrega: String?,
            observaciones: String?,
            cartItems: List<CartItem>
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _pedidoCreated.value = null

            // Validaciones
            if (cartItems.isEmpty()) {
                _error.value = "El carrito está vacío"
                _isLoading.value = false
                return@launch
            }

            if (tipo == 4 && direccionEntrega.isNullOrBlank()) {
                _error.value = "La dirección de entrega es requerida para pedidos a domicilio"
                _isLoading.value = false
                return@launch
            }

            try {
                Log.d(
                        "CheckoutViewModel",
                        "Creating pedido: clienteId=$clienteId, tipo=$tipo, items=${cartItems.size}"
                )

                val result =
                        repo.createPedido(
                                clienteId = clienteId,
                                tipo = tipo,
                                metodoPago = metodoPago,
                                direccionEntrega = direccionEntrega,
                                observaciones = observaciones,
                                cartItems = cartItems
                        )

                result.fold(
                        onSuccess = { pedidoResponse ->
                            Log.d(
                                    "CheckoutViewModel",
                                    "Pedido created: ID=${pedidoResponse.id}, Estado=${pedidoResponse.estado}"
                            )
                            _pedidoCreated.value = pedidoResponse
                        },
                        onFailure = { e ->
                            Log.e("CheckoutViewModel", "Error creating pedido: ${e.message}")
                            _error.value = e.message ?: "Error al crear el pedido"
                        }
                )
            } catch (e: Exception) {
                Log.e("CheckoutViewModel", "Exception creating pedido", e)
                _error.value = e.localizedMessage ?: "Error inesperado al crear el pedido"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Limpia el estado del pedido creado */
    fun clearPedidoCreated() {
        _pedidoCreated.value = null
    }

    /** Limpia el error */
    fun clearError() {
        _error.value = null
    }
}
