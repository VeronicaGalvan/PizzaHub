package com.example.pizzahub_mobile

import android.app.Application
import com.here.sdk.core.engine.AuthenticationMode
import com.here.sdk.core.engine.SDKNativeEngine
import com.here.sdk.core.engine.SDKOptions

class PizzaHubApp : Application() {

    override fun onCreate() {
        super.onCreate()

        val options = SDKOptions(
            AuthenticationMode.withKeySecret(
                BuildConfig.HERE_ACCESS_KEY_ID,
                BuildConfig.HERE_ACCESS_KEY_SECRET
            )
        )

        // Inicializar motor HERE EXPLORE
        SDKNativeEngine.makeSharedInstance(this, options)
    }
}
