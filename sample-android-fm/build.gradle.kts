plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
}

dependencies {
    implementation(project(":modo"))
    implementation(project(":modo-render-android-fm"))
    implementation("com.google.android.material:material:1.4.0")
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
}

android {
    compileSdk = 30
    defaultConfig {
        applicationId = "com.github.terrakok.modo.androidApp"
        minSdk = 24
        targetSdk = 30
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}