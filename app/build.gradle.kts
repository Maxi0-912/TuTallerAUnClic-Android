plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.manuel.tutalleraunclic"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.manuel.tutalleraunclic"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // URL base del backend Django. Cambia según entorno:
        // Emulador → "http://10.0.2.2:8000/"
        // Dispositivo físico (misma red) → "http://192.168.X.X:8000/"
        // Producción → "https://tu-dominio.com/"
        buildConfigField("String", "BASE_URL", "\"https://unthinkingly-unsoporiferous-brentley.ngrok-free.dev/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false // luego lo activas en producción real
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
}

dependencies {

    implementation("com.google.firebase:firebase-messaging:23.4.0")

    // 🔥 BOM (UNA SOLA VEZ)
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))

    // 🔥 CORE
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")

    // 🔥 COMPOSE
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // 🔥 ESTE TE FALTABA (CLAVE)
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")

    // 🔥 VIEWMODEL
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")

    // 🔥 NAVIGATION
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // 🔥 HILT
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // 🔥 DATASTORE
    implementation("androidx.datastore:datastore-preferences:1.0.0")

    // 🔥 COROUTINES
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // 🔥 IMÁGENES
    implementation("io.coil-kt:coil-compose:2.5.0")

    // 🔥 SPLASH
    implementation("androidx.core:core-splashscreen:1.0.1")

    // 🔥 MAPAS
    implementation("com.google.maps.android:maps-compose:4.3.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    // 🔥 NETWORK
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // 🔥 TEST
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    androidTestImplementation(platform("androidx.compose:compose-bom:2024.04.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")

    // 🔥 DEBUG
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}

kapt {
    correctErrorTypes = true
}