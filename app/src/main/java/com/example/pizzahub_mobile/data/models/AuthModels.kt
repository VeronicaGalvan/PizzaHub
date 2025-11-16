package com.example.pizzahub_mobile.data.models

import com.squareup.moshi.Json

data class UserLoginRequest(
        @Json(name = "Email") val email: String,
        @Json(name = "Password") val password: String
)

data class UserRegisterRequest(
        @Json(name = "email") val email: String,
        @Json(name = "password") val password: String,
        @Json(name = "nombreCompleto") val nombreCompleto: String,
        @Json(name = "telefonoContacto") val telefonoContacto: String
)

// DTO used by backend for client registration (RegistroClienteDTO)
data class RegistroClienteRequest(
        @Json(name = "Email") val email: String,
        @Json(name = "Password") val password: String,
        @Json(name = "NumeroCelular") val numeroCelular: String,
        @Json(name = "Nombre") val nombre: String,
        @Json(name = "Apellido") val apellido: String,
        @Json(name = "Colonia") val colonia: String,
        @Json(name = "Calle") val calle: String,
        @Json(name = "Numero") val numero: String,
        @Json(name = "DistanciaAproximada") val distanciaAproximada: Double = 0.0
)

data class UserDto(
        @Json(name = "id") val id: Int,
        @Json(name = "email") val email: String,
        @Json(name = "nombreCompleto") val nombreCompleto: String? = null,
        @Json(name = "telefonoContacto") val telefonoContacto: String? = null
)

// Mirror the server's LoginResponseDTO
// Cliente request for delivery address
data class ClienteRequest(
        @Json(name = "nombre") val nombre: String,
        @Json(name = "apellidos") val apellidos: String,
        @Json(name = "telefono") val telefono: String,
        @Json(name = "colonia") val colonia: String,
        @Json(name = "calle") val calle: String,
        @Json(name = "numeroCasa") val numeroCasa: String,
        @Json(name = "observaciones") val observaciones: String,
        @Json(name = "usuarioId") val usuarioId: Int
)

data class AuthResponse(
        @Json(name = "accessToken") val accessToken: String,
        @Json(name = "refreshToken") val refreshToken: String?,
        @Json(name = "usuario") val usuario: UserDto
)
