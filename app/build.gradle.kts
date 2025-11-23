import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

// Apply the Google services plugin (uses classpath declared in root build.gradle.kts)
apply(plugin = "com.google.gms.google-services")

// Load local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.example.pizzahub_mobile"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.pizzahub_mobile"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            buildConfigField(
                    "String",
                    "HERE_ACCESS_KEY_ID",
                    "\"${localProperties.getProperty("HERE_ACCESS_KEY_ID")}\""
            )
            buildConfigField(
                    "String",
                    "HERE_ACCESS_KEY_SECRET",
                    "\"${localProperties.getProperty("HERE_ACCESS_KEY_SECRET")}\""
            )
            buildConfigField(
                    "String",
                    "GEMINI_API_KEY",
                    "\"${localProperties.getProperty("GEMINI_API_KEY")}\""
            )
        }
        release {
            buildConfigField(
                    "String",
                    "HERE_ACCESS_KEY_ID",
                    "\"${localProperties.getProperty("HERE_ACCESS_KEY_ID")}\""
            )
            buildConfigField(
                    "String",
                    "HERE_ACCESS_KEY_SECRET",
                    "\"${localProperties.getProperty("HERE_ACCESS_KEY_SECRET")}\""
            )
            buildConfigField(
                    "String",
                    "GEMINI_API_KEY",
                    "\"${localProperties.getProperty("GEMINI_API_KEY")}\""
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions { jvmTarget = "11" }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // HERE SDK local dependency
    implementation(files("libs/heresdk-explore-android-4.24.4.0.240388.aar"))

    // Navigation for Compose
    implementation("androidx.navigation:navigation-compose:2.6.0")

    // Coil for image loading in Compose
    implementation("io.coil-kt:coil-compose:2.2.2")

    // Networking: Retrofit + OkHttp + Moshi
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")
    implementation("com.squareup.moshi:moshi:1.15.0")
    // Moshi Kotlin adapter to support Kotlin data classes
    implementation("com.squareup.moshi:moshi-kotlin:1.15.0")

    // DataStore for token persistence
    implementation("androidx.datastore:datastore-preferences:1.1.0")

    // Firebase Cloud Messaging (FCM)
    implementation("com.google.firebase:firebase-messaging:23.2.0")
    
    // Coroutines support for Firebase
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.7.3")

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
