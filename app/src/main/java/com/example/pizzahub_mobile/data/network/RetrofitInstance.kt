package com.example.pizzahub_mobile.data.network

import android.content.Context
import android.content.pm.ApplicationInfo
import com.example.pizzahub_mobile.data.storage.TokenDataStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitInstance {
    // Toggle to true to allow insecure TLS (trust-all). This will only be enabled in debug builds.
    private const val ENABLE_INSECURE_DEBUG_TLS = true

    // Cache del cliente para reutilizar la instancia del AuthApi en el authenticator
    private var cachedRetrofit: Retrofit? = null
    private var cachedAuthApi: AuthApi? = null

    val authApi: AuthApi
        get() {
            if (cachedAuthApi == null) {
                throw IllegalStateException(
                        "RetrofitInstance not initialized. Call create(context) first."
                )
            }
            return cachedAuthApi!!
        }

    fun create(context: Context): Retrofit {
        if (cachedRetrofit != null) {
            return cachedRetrofit!!
        }

        val logging = HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }

        val authInterceptor = Interceptor { chain ->
            val original = chain.request()
            val builder: Request.Builder = original.newBuilder()
            val token = TokenDataStore.getAccessTokenBlocking(context)
            if (!token.isNullOrBlank()) {
                builder.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(builder.build())
        }

        val clientBuilder =
                OkHttpClient.Builder()
                        .addInterceptor(authInterceptor)
                        .addInterceptor(logging)
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)

        // If we're in debug mode and using localhost, install a permissive TrustManager for
        // self-signed certs
        val isLocalhost =
                NetworkConfig.BASE_URL.contains("10.0.2.2") ||
                        NetworkConfig.BASE_URL.contains("localhost")
        if (isDebuggable(context) && ENABLE_INSECURE_DEBUG_TLS && isLocalhost) {
            try {
                val trustAllCerts =
                        arrayOf<TrustManager>(
                                object : X509TrustManager {
                                    override fun checkClientTrusted(
                                            chain: Array<X509Certificate>,
                                            authType: String
                                    ) {}
                                    override fun checkServerTrusted(
                                            chain: Array<X509Certificate>,
                                            authType: String
                                    ) {}
                                    override fun getAcceptedIssuers(): Array<X509Certificate> =
                                            arrayOf()
                                }
                        )

                val sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, trustAllCerts, SecureRandom())
                val sslSocketFactory = sslContext.socketFactory

                val hostnameVerifier = HostnameVerifier { _: String?, _: SSLSession? -> true }

                clientBuilder
                        .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                        .hostnameVerifier(hostnameVerifier)
            } catch (e: Exception) {
                // If any of this fails, fall back to default client; log if needed
                e.printStackTrace()
            }
        }

        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        // Crear una instancia temporal de Retrofit para obtener AuthApi para el authenticator
        val tempRetrofit =
                Retrofit.Builder()
                        .baseUrl(NetworkConfig.BASE_URL)
                        .client(clientBuilder.build())
                        .addConverterFactory(MoshiConverterFactory.create(moshi))
                        .build()

        val authApi = tempRetrofit.create(AuthApi::class.java)
        cachedAuthApi = authApi

        // Ahora agregar el authenticator con la instancia de AuthApi
        val tokenAuthenticator = TokenAuthenticator(context, authApi)
        clientBuilder.authenticator(tokenAuthenticator)

        val client = clientBuilder.build()

        val retrofit =
                Retrofit.Builder()
                        .baseUrl(NetworkConfig.BASE_URL)
                        .client(client)
                        .addConverterFactory(MoshiConverterFactory.create(moshi))
                        .build()

        cachedRetrofit = retrofit
        return retrofit
    }

    private fun isDebuggable(context: Context): Boolean {
        return (context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0
    }
}
