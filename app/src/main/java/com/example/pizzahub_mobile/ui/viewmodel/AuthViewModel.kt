package com.example.pizzahub_mobile.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzahub_mobile.data.network.AuthApi
import com.example.pizzahub_mobile.data.network.AuthRepository
import com.example.pizzahub_mobile.data.network.RetrofitInstance
import com.example.pizzahub_mobile.data.storage.TokenDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val ctx = application.applicationContext
    private val api: AuthApi = RetrofitInstance.create(ctx).create(AuthApi::class.java)
    private val repo = AuthRepository(api, ctx)

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated

    private val _currentUser =
            MutableStateFlow<com.example.pizzahub_mobile.data.models.UserDto?>(null)
    val currentUser: StateFlow<com.example.pizzahub_mobile.data.models.UserDto?> = _currentUser

    fun checkExistingToken() {
        viewModelScope.launch {
            val t = TokenDataStore.getAccessTokenBlocking(ctx)
            _isAuthenticated.value = !t.isNullOrBlank()
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            when (val r = repo.login(email, password)) {
                is com.example.pizzahub_mobile.data.network.AuthResult.Success -> {
                    _isAuthenticated.value = true
                    _currentUser.value = r.user as? com.example.pizzahub_mobile.data.models.UserDto
                }
                is com.example.pizzahub_mobile.data.network.AuthResult.Failure -> {
                    _error.value = r.message
                }
            }
            _isLoading.value = false
        }
    }

    fun register(
            email: String,
            password: String,
            nombreCompleto: String,
            telefonoContacto: String
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            when (val r = repo.register(email, password, nombreCompleto, telefonoContacto)) {
                is com.example.pizzahub_mobile.data.network.AuthResult.Success -> {
                    _isAuthenticated.value = true
                    _currentUser.value = r.user as? com.example.pizzahub_mobile.data.models.UserDto
                }
                is com.example.pizzahub_mobile.data.network.AuthResult.Failure -> {
                    _error.value = r.message
                }
            }
            _isLoading.value = false
        }
    }

    fun createCliente(
            nombre: String,
            apellidos: String,
            telefono: String,
            colonia: String,
            calle: String,
            numeroCasa: String,
            observaciones: String,
            usuarioId: Int
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result =
                    repo.createCliente(
                            nombre,
                            apellidos,
                            telefono,
                            colonia,
                            calle,
                            numeroCasa,
                            observaciones,
                            usuarioId
                    )
            if (result.isFailure) {
                _error.value = result.exceptionOrNull()?.message ?: "Error al guardar dirección"
            }
            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            TokenDataStore.clear(ctx)
            _isAuthenticated.value = false
            _currentUser.value = null
        }
    }
}
