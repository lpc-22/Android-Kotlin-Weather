plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    namespace = "com.pc.weather_puichuen"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.pc.weather_puichuen"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
    configurations{
        implementation.get().exclude(mapOf("group" to "org.jetbrains", "module" to "annotations"))
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.google.android.gms:play-services-location:21.0.1")

    // Add library imports for Retrofit, Moshi, HttpLoggingInterceptor, Kotlin Coroutines
    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // moshi
    implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
    // moshi annotation processor for kotlin
    implementation("com.squareup.moshi:moshi-kotlin:1.9.3")
    // optional library to debug Retrofit’s http requests/responses
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.3")

    //for Room components
    implementation ("androidx.room:room-runtime:2.4.0-alpha04")
    implementation ("androidx.room:room-ktx:2.4.0-alpha04")
    kapt ("androidx.room:room-compiler:2.4.0-alpha04")

    // needed for background tasks
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-service:2.6.2")
    // Lifecycle components
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-compiler:2.6.2")
}