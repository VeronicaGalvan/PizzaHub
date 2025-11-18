package com.example.pizzahub_mobile.data.network

import android.content.Context
import com.example.pizzahub_mobile.data.models.ClientePerfilResponse
import com.example.pizzahub_mobile.data.models.ClienteRequest
import com.example.pizzahub_mobile.data.models.ClienteUpdateRequest
import com.example.pizzahub_mobile.data.models.RefreshTokenRequest
import com.example.pizzahub_mobile.data.models.UserLoginRequest
import com.example.pizzahub_mobile.data.models.UserRegisterRequest
import com.example.pizzahub_mobile.data.storage.TokenDataStore

sealed class AuthResult {
    data class Success(val accessToken: String, val refreshToken: String?, val user: Any?) :
            AuthResult()
    data class Failure(val message: String) : AuthResult()
}

class AuthRepository(private val api: AuthApi, private val context: Context) {
    suspend fun login(email: String, password: String): AuthResult =
            try {
                val resp = api.login(UserLoginRequest(email, password))
                if (resp.isSuccessful) {
                    val body = resp.body()!!
                    TokenDataStore.saveTokens(context, body.accessToken, body.refreshToken)
                    AuthResult.Success(body.accessToken, body.refreshToken, body.usuario)
                } else {
                    val msg = resp.errorBody()?.string() ?: "${resp.code()}: ${resp.message()}"
                    AuthResult.Failure(msg)
                }
            } catch (e: Exception) {
                AuthResult.Failure(e.localizedMessage ?: "Network error")
            }

    suspend fun register(
            email: String,
            password: String,
            nombreUsuario: String,
            telefonoContacto: String
    ): AuthResult =
            try {
                val req =
                        UserRegisterRequest(
                                email = email,
                                password = password,
                                nombreUsuario = nombreUsuario,
                                telefonoContacto = telefonoContacto
                        )
                val resp = api.register(req)
                if (resp.isSuccessful) {
                    val body = resp.body()!!
                    TokenDataStore.saveTokens(context, body.accessToken, body.refreshToken)
                    AuthResult.Success(body.accessToken, body.refreshToken, body.usuario)
                } else {
                    val msg = resp.errorBody()?.string() ?: "${resp.code()}: ${resp.message()}"
                    AuthResult.Failure(msg)
                }
            } catch (e: Exception) {
                AuthResult.Failure(e.localizedMessage ?: "Network error")
            }

    suspend fun refreshToken(refreshToken: String): AuthResult =
            try {
                val resp = api.refreshToken(RefreshTokenRequest(refreshToken))
                if (resp.isSuccessful) {
                    val body = resp.body()!!
                    TokenDataStore.saveTokens(context, body.accessToken, body.refreshToken)
                    AuthResult.Success(body.accessToken, body.refreshToken, body.usuario)
                } else {
                    val msg = resp.errorBody()?.string() ?: "${resp.code()}: ${resp.message()}"
                    AuthResult.Failure(msg)
                }
            } catch (e: Exception) {
                AuthResult.Failure(e.localizedMessage ?: "Network error")
            }

    suspend fun logout(): Result<Unit> =
            try {
                val token = TokenDataStore.getAccessTokenBlocking(context)
                if (!token.isNullOrBlank()) {
                    val resp = api.logout("Bearer $token")
                    if (resp.isSuccessful) {
                        TokenDataStore.clear(context)
                        Result.success(Unit)
                    } else {
                        // Aunque falle el backend, limpiamos tokens localmente
                        TokenDataStore.clear(context)
                        val msg = resp.errorBody()?.string() ?: "${resp.code()}: ${resp.message()}"
                        Result.failure(Exception(msg))
                    }
                } else {
                    TokenDataStore.clear(context)
                    Result.success(Unit)
                }
            } catch (e: Exception) {
                // Aunque falle, limpiamos tokens localmente
                TokenDataStore.clear(context)
                Result.failure(e)
            }

    suspend fun createCliente(
            nombre: String,
            apellidos: String,
            telefono: String,
            colonia: String,
            calle: String,
            numeroCasa: String,
            observaciones: String,
            usuarioId: Int
    ): Result<Unit> =
            try {
                val req =
                        ClienteRequest(
                                nombre = nombre,
                                apellidos = apellidos,
                                telefono = telefono,
                                colonia = colonia,
                                calle = calle,
                                numeroCasa = numeroCasa,
                                observaciones = observaciones,
                                usuarioId = usuarioId
                        )
                val resp = api.createCliente(req)
                if (resp.isSuccessful) {
                    Result.success(Unit)
                } else {
                    val msg = resp.errorBody()?.string() ?: "${resp.code()}: ${resp.message()}"
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }

    suspend fun getClientePerfil(): Result<ClientePerfilResponse> =
            try {
                val resp = api.getClientePerfil()
                if (resp.isSuccessful && resp.body() != null) {
                    Result.success(resp.body()!!)
                } else {
                    val msg = resp.errorBody()?.string() ?: "${resp.code()}: ${resp.message()}"
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }

    suspend fun updateClientePerfil(
            nombre: String,
            apellidos: String,
            telefono: String,
            colonia: String,
            calle: String,
            numeroCasa: String,
            observaciones: String
    ): Result<Unit> =
            try {
                val req =
                        ClienteUpdateRequest(
                                nombre = nombre,
                                apellidos = apellidos,
                                telefono = telefono,
                                colonia = colonia,
                                calle = calle,
                                numeroCasa = numeroCasa,
                                observaciones = observaciones
                        )
                val resp = api.updateClientePerfil(req)
                if (resp.isSuccessful) {
                    Result.success(Unit)
                } else {
                    val msg = resp.errorBody()?.string() ?: "${resp.code()}: ${resp.message()}"
                    Result.failure(Exception(msg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
}
