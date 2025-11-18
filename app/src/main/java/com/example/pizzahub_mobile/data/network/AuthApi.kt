package com.example.pizzahub_mobile.data.network

import com.example.pizzahub_mobile.data.models.AuthResponse
import com.example.pizzahub_mobile.data.models.ClientePerfilResponse
import com.example.pizzahub_mobile.data.models.ClienteRequest
import com.example.pizzahub_mobile.data.models.ClienteUpdateRequest
import com.example.pizzahub_mobile.data.models.RefreshTokenRequest
import com.example.pizzahub_mobile.data.models.UserLoginRequest
import com.example.pizzahub_mobile.data.models.UserRegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

interface AuthApi {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: UserLoginRequest): Response<AuthResponse>

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: UserRegisterRequest): Response<AuthResponse>

    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AuthResponse>

    @POST("api/v1/auth/logout")
    suspend fun logout(@Header("Authorization") authorization: String): Response<Unit>

    @POST("api/Clientes") suspend fun createCliente(@Body request: ClienteRequest): Response<Any>

    @GET("api/Clientes/mi-perfil") suspend fun getClientePerfil(): Response<ClientePerfilResponse>

    @PUT("api/Clientes/mi-perfil")
    suspend fun updateClientePerfil(@Body request: ClienteUpdateRequest): Response<Any>
}
