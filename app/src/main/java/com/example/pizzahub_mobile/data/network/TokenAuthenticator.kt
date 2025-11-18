package com.example.pizzahub_mobile.data.network

import android.content.Context
import com.example.pizzahub_mobile.data.models.RefreshTokenRequest
import com.example.pizzahub_mobile.data.storage.TokenDataStore
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

/**
 * Authenticator que intercepta errores 401 (Unauthorized) y refresca automáticamente el token
 * usando el refresh token almacenado. Si el refresh falla, devuelve null para que se cierre sesión.
 */
class TokenAuthenticator(private val context: Context, private val authApi: AuthApi) :
        Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        // Si ya intentamos refrescar el token y volvió a fallar, no intentar de nuevo
        if (response.request.header("Authorization") != null && response.priorResponse?.code == 401
        ) {
            // Ya intentamos refrescar y falló de nuevo - cerrar sesión
            return null
        }

        // Obtener el refresh token
        val refreshToken = TokenDataStore.getRefreshTokenBlocking(context)
        if (refreshToken.isNullOrBlank()) {
            // No hay refresh token, no podemos refrescar - cerrar sesión
            return null
        }

        // Intentar refrescar el token de forma síncrona
        return runBlocking {
            try {
                val refreshResponse = authApi.refreshToken(RefreshTokenRequest(refreshToken))
                if (refreshResponse.isSuccessful && refreshResponse.body() != null) {
                    val body = refreshResponse.body()!!
                    // Guardar los nuevos tokens
                    TokenDataStore.saveTokens(context, body.accessToken, body.refreshToken)

                    // Reintentar la petición original con el nuevo token
                    response.request
                            .newBuilder()
                            .header("Authorization", "Bearer ${body.accessToken}")
                            .build()
                } else {
                    // El refresh token expiró o es inválido - cerrar sesión
                    TokenDataStore.clear(context)
                    null
                }
            } catch (e: Exception) {
                // Error al refrescar - cerrar sesión
                TokenDataStore.clear(context)
                null
            }
        }
    }
}
