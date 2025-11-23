package com.example.pizzahub_mobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzahub_mobile.data.models.CalificacionRequest
import com.example.pizzahub_mobile.data.network.AuthRepository
import com.example.pizzahub_mobile.data.network.RetrofitInstance
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RatingViewModel(application: Application) : AndroidViewModel(application) {
    private val repository =
            AuthRepository(RetrofitInstance.authApi, application.applicationContext)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _success = MutableStateFlow(false)
    val success: StateFlow<Boolean> = _success

    fun submitCalificacion(pedidoId: Int, estrellas: Int, comentario: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _success.value = false

            try {
                val request = CalificacionRequest(estrellas = estrellas, comentario = comentario)
                val response = repository.createCalificacion(pedidoId, request)

                if (response.isSuccessful) {
                    _success.value = true
                } else {
                    _error.value = "Error al enviar calificación: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = "Error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
