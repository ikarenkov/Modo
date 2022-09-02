plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
}

android {
    compileSdk = (properties["android.compileSdk"] as String).toInt()

    defaultConfig {
        applicationId = "com.github.terrakok.androidcomposeapp"
        minSdk = (properties["android.minSdk"] as String).toInt()
        targetSdk = (properties["android.targetSdk"] as String).toInt()
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = properties["version.kotlinCompilerExtension"] as String
    }
}

dependencies {
    implementation(project(":modo"))
    implementation(project(":modo-render-android-compose"))
    implementation("androidx.core:core-ktx:${properties["version.coreKtx"]}")
    implementation("androidx.appcompat:appcompat:${properties["version.appcompat"]}")
    implementation("com.google.android.material:material:${properties["version.material"]}")
    implementation("androidx.compose.ui:ui:${properties["version.compose"]}")
    implementation("androidx.compose.material:material:${properties["version.compose"]}")
    implementation("androidx.compose.ui:ui-tooling:${properties["version.compose"]}")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${properties["version.lifecycleRuntimeKtx"]}")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:${properties["version.composeViewModel"]}")
    implementation("androidx.activity:activity-compose:${properties["version.composeActivity"]}")
}