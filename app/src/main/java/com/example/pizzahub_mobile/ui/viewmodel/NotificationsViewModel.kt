package com.example.pizzahub_mobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzahub_mobile.data.models.NotificacionDto
import com.example.pizzahub_mobile.data.network.AuthApi
import com.example.pizzahub_mobile.data.network.AuthRepository
import com.example.pizzahub_mobile.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NotificationsViewModel(application: Application) : AndroidViewModel(application) {
    private val ctx = application.applicationContext
    private val api: AuthApi = RetrofitInstance.create(ctx).create(AuthApi::class.java)
    private val repo = AuthRepository(api, ctx)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _notificaciones = MutableStateFlow<List<NotificacionDto>>(emptyList())
    val notificaciones: StateFlow<List<NotificacionDto>> = _notificaciones

    private val _unreadCount = MutableStateFlow(0)
    val unreadCount: StateFlow<Int> = _unreadCount

    init {
        // El backend ahora obtiene el cliente desde el JWT, no es necesario pasar clienteId
        loadNotificaciones()
        loadUnreadCount()
    }

    fun loadNotificaciones() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repo.getNotificaciones()
            result.fold(
                    onSuccess = { list -> _notificaciones.value = list },
                    onFailure = { e ->
                        _error.value = e.message ?: "Error al cargar notificaciones"
                    }
            )
            _isLoading.value = false
        }
    }

    fun loadUnreadCount() {
        viewModelScope.launch {
            val result = repo.getNotificacionesNoLeidasConteo()
            result.fold(onSuccess = { count -> _unreadCount.value = count }, onFailure = {})
        }
    }

    fun marcarLeida(notificacionId: Int) {
        viewModelScope.launch {
            val result = repo.marcarNotificacionLeida(notificacionId)
            if (result.isSuccess) {
                // Actualizar localmente
                _notificaciones.value =
                        _notificaciones.value.map { notif ->
                            if (notif.id == notificacionId) notif.copy(leida = true) else notif
                        }
                loadUnreadCount()
            }
        }
    }

    fun marcarTodasLeidas() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repo.marcarTodasNotificacionesLeidas()
            if (result.isSuccess) {
                // Actualizar localmente
                _notificaciones.value = _notificaciones.value.map { it.copy(leida = true) }
                _unreadCount.value = 0
            } else {
                _error.value = "Error al marcar todas como leídas"
            }
            _isLoading.value = false
        }
    }

    fun clearError() {
        _error.value = null
    }
}
