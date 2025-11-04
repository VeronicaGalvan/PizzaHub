package com.example.pizzahub_mobile.data.network

object TokenStore {
    // In-memory token for now. Later persist with DataStore.
    @Volatile var accessToken: String? = null
    @Volatile var refreshToken: String? = null

    fun clear() {
        accessToken = null
        refreshToken = null
    }
}
