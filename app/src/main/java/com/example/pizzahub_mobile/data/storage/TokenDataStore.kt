package com.example.pizzahub_mobile.data.storage

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "auth_prefs")

object TokenDataStore {
    private val ACCESS_TOKEN = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    private val FCM_TOKEN = stringPreferencesKey("fcm_token")

    fun getAccessTokenFlow(context: Context): Flow<String?> =
            context.dataStore.data.map { prefs -> prefs[ACCESS_TOKEN] }

    fun getRefreshTokenFlow(context: Context): Flow<String?> =
            context.dataStore.data.map { prefs -> prefs[REFRESH_TOKEN] }

    fun getFcmTokenFlow(context: Context): Flow<String?> =
            context.dataStore.data.map { prefs -> prefs[FCM_TOKEN] }

    suspend fun saveTokens(context: Context, access: String, refresh: String?) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN] = access
            if (refresh != null) prefs[REFRESH_TOKEN] = refresh
        }
    }

    suspend fun saveFcmToken(context: Context, token: String) {
        context.dataStore.edit { prefs -> prefs[FCM_TOKEN] = token }
    }

    suspend fun clear(context: Context) {
        context.dataStore.edit { prefs ->
            prefs.remove(ACCESS_TOKEN)
            prefs.remove(REFRESH_TOKEN)
            prefs.remove(FCM_TOKEN)
        }
    }

    // blocking utility for interceptor (not ideal but pragmatic)
    fun getAccessTokenBlocking(context: Context): String? {
        return try {
            kotlinx.coroutines.runBlocking { getAccessTokenFlow(context).first() }
        } catch (e: Exception) {
            null
        }
    }

    fun getRefreshTokenBlocking(context: Context): String? {
        return try {
            kotlinx.coroutines.runBlocking { getRefreshTokenFlow(context).first() }
        } catch (e: Exception) {
            null
        }
    }

    fun getFcmTokenBlocking(context: Context): String? {
        return try {
            kotlinx.coroutines.runBlocking { getFcmTokenFlow(context).first() }
        } catch (e: Exception) {
            null
        }
    }
}
