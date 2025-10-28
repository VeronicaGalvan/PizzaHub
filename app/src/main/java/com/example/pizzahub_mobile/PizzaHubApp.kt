package com.example.pizzahub_mobile

import android.app.Application

// Clase Application principal. Si vas a usar Hilt, añade la anotación @HiltAndroidApp
class PizzaHubApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // Inicializaciones globales aquí (logging, crashlytics, DI, etc.)
    }
}

