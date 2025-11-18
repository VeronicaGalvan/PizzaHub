package com.example.pizzahub_mobile.data.models

import com.squareup.moshi.Json

data class UserLoginRequest(
        @Json(name = "Email") val email: String,
        @Json(name = "Password") val password: String
)

data class UserRegisterRequest(
        @Json(name = "email") val email: String,
        @Json(name = "password") val password: String,
        @Json(name = "nombreUsuario") val nombreUsuario: String,
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
// Cliente request for delivery address (POST /api/Clientes)
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

// Cliente update request (PUT /api/Clientes/mi-perfil)
data class ClienteUpdateRequest(
        @Json(name = "nombre") val nombre: String,
        @Json(name = "apellidos") val apellidos: String,
        @Json(name = "telefono") val telefono: String,
        @Json(name = "colonia") val colonia: String,
        @Json(name = "calle") val calle: String,
        @Json(name = "numeroCasa") val numeroCasa: String,
        @Json(name = "observaciones") val observaciones: String
)

// Cliente response (GET /api/Clientes/mi-perfil)
data class ClientePerfilResponse(
        @Json(name = "id") val id: Int,
        @Json(name = "nombre") val nombre: String,
        @Json(name = "apellidos") val apellidos: String,
        @Json(name = "telefono") val telefono: String,
        @Json(name = "usuarioId") val usuarioId: Int,
        @Json(name = "colonia") val colonia: String? = null,
        @Json(name = "calle") val calle: String? = null,
        @Json(name = "numeroCasa") val numeroCasa: String? = null,
        @Json(name = "observaciones") val observaciones: String? = null,
        @Json(name = "usuario") val usuario: UsuarioInfo? = null
)

data class UsuarioInfo(
        @Json(name = "id") val id: Int,
        @Json(name = "nombreUsuario") val nombreUsuario: String,
        @Json(name = "telefono") val telefono: String,
        @Json(name = "correo") val correo: String,
        @Json(name = "rol") val rol: Int,
        @Json(name = "activo") val activo: Boolean,
        @Json(name = "fechaCreacion") val fechaCreacion: String
)

data class AuthResponse(
        @Json(name = "accessToken") val accessToken: String,
        @Json(name = "refreshToken") val refreshToken: String?,
        @Json(name = "tokenType") val tokenType: String? = "Bearer",
        @Json(name = "expiresIn") val expiresIn: Int? = null,
        @Json(name = "roles") val roles: List<String>? = null,
        @Json(name = "usuario") val usuario: UserDto
)

data class RefreshTokenRequest(@Json(name = "refreshToken") val refreshToken: String)
