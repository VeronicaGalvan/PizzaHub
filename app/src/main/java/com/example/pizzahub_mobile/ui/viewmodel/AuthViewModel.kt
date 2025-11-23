package com.example.pizzahub_mobile.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pizzahub_mobile.data.models.ClientePerfilResponse
import com.example.pizzahub_mobile.data.network.AuthApi
import com.example.pizzahub_mobile.data.network.AuthRepository
import com.example.pizzahub_mobile.data.network.RetrofitInstance
import com.example.pizzahub_mobile.data.storage.TokenDataStore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

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

    private val _clientePerfil = MutableStateFlow<ClientePerfilResponse?>(null)
    val clientePerfil: StateFlow<ClientePerfilResponse?> = _clientePerfil

    init {
        // Observar cambios en el token para detectar cuando se limpia (por token expirado)
        viewModelScope.launch {
            TokenDataStore.getAccessTokenFlow(ctx).collect { token ->
                val wasAuthenticated = _isAuthenticated.value
                val isNowAuthenticated = !token.isNullOrBlank()

                _isAuthenticated.value = isNowAuthenticated

                // Si el usuario estaba autenticado y el token se limpió, limpiar estado
                if (wasAuthenticated && !isNowAuthenticated) {
                    _currentUser.value = null
                    _clientePerfil.value = null
                }
            }
        }
    }

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

                    // Registrar token FCM después de login exitoso
                    registerFcmToken()
                }
                is com.example.pizzahub_mobile.data.network.AuthResult.Failure -> {
                    _error.value = r.message
                }
            }
            _isLoading.value = false
        }
    }

    fun register(email: String, password: String, nombreUsuario: String, telefonoContacto: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            when (val r = repo.register(email, password, nombreUsuario, telefonoContacto)) {
                is com.example.pizzahub_mobile.data.network.AuthResult.Success -> {
                    _isAuthenticated.value = true
                    _currentUser.value = r.user as? com.example.pizzahub_mobile.data.models.UserDto

                    // Registrar token FCM después de registro exitoso
                    registerFcmToken()
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

    fun getClientePerfil() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repo.getClientePerfil()
            result.fold(
                    onSuccess = { perfil -> _clientePerfil.value = perfil },
                    onFailure = { e -> _error.value = e.message ?: "Error al cargar perfil" }
            )
            _isLoading.value = false
        }
    }

    fun updateClientePerfil(
            nombre: String,
            apellidos: String,
            telefono: String,
            colonia: String,
            calle: String,
            numeroCasa: String,
            observaciones: String,
            onSuccess: () -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result =
                    repo.updateClientePerfil(
                            nombre,
                            apellidos,
                            telefono,
                            colonia,
                            calle,
                            numeroCasa,
                            observaciones
                    )
            result.fold(
                    onSuccess = {
                        // Recargar perfil después de actualizar
                        getClientePerfil()
                        onSuccess()
                    },
                    onFailure = { e -> _error.value = e.message ?: "Error al actualizar perfil" }
            )
            _isLoading.value = false
        }
    }

    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            // Intentar eliminar token FCM en el backend (si existe)
            try {
                val fcm = TokenDataStore.getFcmTokenBlocking(ctx)
                if (!fcm.isNullOrBlank()) {
                    repo.eliminarTokenFcm(fcm)
                }
            } catch (e: Exception) {
                // Ignorar errores de borrado de token; proceder con logout
            }

            // Intentar cerrar sesión en el backend
            repo.logout()
            // Limpiar estado local
            _isAuthenticated.value = false
            _currentUser.value = null
            _clientePerfil.value = null
            _isLoading.value = false
        }
    }

    /**
     * Obtiene y registra el token FCM en el backend. Se llama automáticamente después de
     * login/registro exitoso. Incluye reintentos en caso de fallo.
     */
    private fun registerFcmToken(retryCount: Int = 0) {
        viewModelScope.launch {
            try {
                // Obtener token FCM de Firebase
                val token = FirebaseMessaging.getInstance().token.await()
                Log.d("AuthViewModel", "FCM Token obtenido: $token")

                // Guardar localmente
                TokenDataStore.saveFcmToken(ctx, token)

                // Enviar al backend
                val result = repo.registrarTokenFcm(token)
                if (result.isSuccess) {
                    Log.d("AuthViewModel", "Token FCM registrado exitosamente en backend")
                } else {
                    Log.e(
                            "AuthViewModel",
                            "Error al registrar token FCM: ${result.exceptionOrNull()?.message}"
                    )
                    // Reintentar hasta 3 veces
                    if (retryCount < 3) {
                        Log.d(
                                "AuthViewModel",
                                "Reintentando registro de token... intento ${retryCount + 1}"
                        )
                        kotlinx.coroutines.delay(2000L * (retryCount + 1)) // Delay exponencial
                        registerFcmToken(retryCount + 1)
                    }
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Excepción al obtener/registrar token FCM", e)
                // Reintentar hasta 3 veces
                if (retryCount < 3) {
                    kotlinx.coroutines.delay(2000L * (retryCount + 1))
                    registerFcmToken(retryCount + 1)
                }
            }
        }
    }
}
