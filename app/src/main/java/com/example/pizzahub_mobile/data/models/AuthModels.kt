package com.example.pizzahub_mobile.data.models

import com.squareup.moshi.Json

data class UserLoginRequest(
        @Json(name = "Email") val email: String,
        @Json(name = "Password") val password: String
)

data class UserRegisterRequest(
        @Json(name = "NombreCompleto") val name: String,
        @Json(name = "Email") val email: String,
        @Json(name = "Password") val password: String,
        @Json(name = "TelefonoContacto") val telefonoContacto: String? = null
)

data class UserDto(
        @Json(name = "id") val id: Int,
        @Json(name = "email") val email: String,
        @Json(name = "nombreCompleto") val nombreCompleto: String? = null,
        @Json(name = "telefonoContacto") val telefonoContacto: String? = null
)

// Mirror the server's LoginResponseDTO
data class AuthResponse(
        @Json(name = "accessToken") val accessToken: String,
        @Json(name = "refreshToken") val refreshToken: String?,
        @Json(name = "usuario") val usuario: UserDto
)
