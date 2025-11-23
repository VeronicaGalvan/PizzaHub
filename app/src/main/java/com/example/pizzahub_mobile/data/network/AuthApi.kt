package com.example.pizzahub_mobile.data.network

import com.example.pizzahub_mobile.data.models.AuthResponse
import com.example.pizzahub_mobile.data.models.CalificacionRequest
import com.example.pizzahub_mobile.data.models.ClientePerfilResponse
import com.example.pizzahub_mobile.data.models.ClienteRequest
import com.example.pizzahub_mobile.data.models.ClienteUpdateRequest
import com.example.pizzahub_mobile.data.models.PedidoRequest
import com.example.pizzahub_mobile.data.models.PedidoResponse
import com.example.pizzahub_mobile.data.models.RefreshTokenRequest
import com.example.pizzahub_mobile.data.models.UserLoginRequest
import com.example.pizzahub_mobile.data.models.UserRegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HTTP
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface AuthApi {
        @POST("api/v1/auth/login")
        suspend fun login(@Body request: UserLoginRequest): Response<AuthResponse>

        @POST("api/v1/auth/register")
        suspend fun register(@Body request: UserRegisterRequest): Response<AuthResponse>

        @POST("api/v1/auth/refresh")
        suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<AuthResponse>

        @POST("api/v1/auth/logout")
        suspend fun logout(@Header("Authorization") authorization: String): Response<Unit>

        @POST("api/Clientes")
        suspend fun createCliente(@Body request: ClienteRequest): Response<Any>

        @GET("api/Clientes/mi-perfil")
        suspend fun getClientePerfil(): Response<ClientePerfilResponse>

        @PUT("api/Clientes/mi-perfil")
        suspend fun updateClientePerfil(@Body request: ClienteUpdateRequest): Response<Any>

        @POST("api/PedidosNew/registrar")
        suspend fun createPedido(@Body request: PedidoRequest): Response<PedidoResponse>

        @GET("api/PedidosNew/{id}")
        suspend fun getPedidoById(@Path("id") pedidoId: Int): Response<PedidoResponse>

        @GET("api/PedidosNew/cliente/{clienteId}")
        suspend fun getPedidosByCliente(
                @Path("clienteId") clienteId: Int
        ): Response<List<PedidoResponse>>

        @POST("api/PedidosNew/{id}/repetir")
        suspend fun repetirPedido(@Path("id") pedidoId: Int): Response<PedidoResponse>

        @POST("api/Calificaciones/pedido/{pedidoId}")
        suspend fun createCalificacion(
                @Path("pedidoId") pedidoId: Int,
                @Body request: CalificacionRequest
        ): Response<Any>

        // Notifications endpoints
        @POST("api/Notificaciones/registrar-token")
        suspend fun registrarToken(
                @Body body: com.example.pizzahub_mobile.data.models.RegistrarTokenFCMDto
        ): Response<Any>

        // DELETE with body — Retrofit doesn't support body on @DELETE directly so use @HTTP
        @HTTP(method = "DELETE", path = "api/Notificaciones/eliminar-token", hasBody = true)
        suspend fun eliminarToken(
                @Body body: com.example.pizzahub_mobile.data.models.RegistrarTokenFCMDto
        ): Response<Any>

        @GET("api/Notificaciones")
        suspend fun getNotificaciones():
                Response<List<com.example.pizzahub_mobile.data.models.NotificacionDto>>

        @GET("api/Notificaciones/no-leidas/conteo")
        suspend fun getNotificacionesNoLeidasConteo(): Response<Int>

        @PUT("api/Notificaciones/{id}/marcar-leida")
        suspend fun marcarNotificacionLeida(@Path("id") id: Int): Response<Any>

        @PUT("api/Notificaciones/marcar-todas-leidas")
        suspend fun marcarTodasNotificacionesLeidas(): Response<Any>

        @POST("api/Notificaciones/prueba")
        suspend fun pruebaNotificacion(
                @Body body: com.example.pizzahub_mobile.data.models.RegistrarTokenFCMDto
        ): Response<Any>
}
