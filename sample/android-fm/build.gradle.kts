plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
}

dependencies {
    implementation(project(":modo"))
    implementation(project(":modo-render-android-fm"))
    implementation("com.google.android.material:material:${properties["version.material"]}")
    implementation("androidx.appcompat:appcompat:${properties["version.appcompat"]}")
    implementation("androidx.constraintlayout:constraintlayout:${properties["version.constraint"]}")
}

android {
    compileSdk = (properties["android.compileSdk"] as String).toInt()
    defaultConfig {
        applicationId = "com.github.terrakok.modo.androidApp"
        minSdk = (properties["android.minSdk"] as String).toInt()
        targetSdk = (properties["android.targetSdk"] as String).toInt()
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}