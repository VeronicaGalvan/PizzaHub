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
                }
                is com.example.pizzahub_mobile.data.network.AuthResult.Failure -> {
                    _error.value = r.message
                }
            }
            _isLoading.value = false
        }
    }

    fun register(name: String, email: String, password: String, telefono: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            when (val r = repo.register(name, email, password, telefono)) {
                is com.example.pizzahub_mobile.data.network.AuthResult.Success ->
                        _isAuthenticated.value = true
                is com.example.pizzahub_mobile.data.network.AuthResult.Failure ->
                        _error.value = r.message
            }
            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            TokenDataStore.clear(ctx)
            _isAuthenticated.value = false
        }
    }
}
