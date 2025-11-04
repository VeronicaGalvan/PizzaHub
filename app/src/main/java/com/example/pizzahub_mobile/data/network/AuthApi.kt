package com.example.pizzahub_mobile.data.network

import com.example.pizzahub_mobile.data.models.AuthResponse
import com.example.pizzahub_mobile.data.models.UserLoginRequest
import com.example.pizzahub_mobile.data.models.UserRegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/auth/login")
    suspend fun login(@Body request: UserLoginRequest): Response<AuthResponse>

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: UserRegisterRequest): Response<AuthResponse>
}
