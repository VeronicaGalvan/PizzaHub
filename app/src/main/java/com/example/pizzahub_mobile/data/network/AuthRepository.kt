package com.example.pizzahub_mobile.data.network

import android.content.Context
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
            name: String,
            email: String,
            password: String,
            telefono: String?
    ): AuthResult =
            try {
                val req = UserRegisterRequest(name, email, password, telefono)
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
}
