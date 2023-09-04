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
    val composeBom = platform("androidx.compose:compose-bom:${properties["version.composeBom"]}")
    implementation(composeBom)
    androidTestImplementation(composeBom)

    implementation(project(":modo-compose"))
    implementation("androidx.core:core-ktx:${properties["version.coreKtx"]}")
    implementation("androidx.appcompat:appcompat:${properties["version.appcompat"]}")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:${properties["version.lifecycleRuntimeKtx"]}")
    implementation("androidx.activity:activity-compose:${properties["version.composeActivity"]}")

    implementation("androidx.compose.material:material")

    implementation("com.squareup.logcat:logcat:0.1")
}