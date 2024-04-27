import com.github.terrakok.configureJetpackCompose
import com.github.terrakok.configureKotlinAndroid

plugins {
    id("com.android.application")
    kotlin("android")
    id("kotlin-parcelize")
    id("build-logic")
}

android {
    namespace = "com.github.terrakok.androidcomposeapp"

    configureKotlinAndroid(this)
    configureJetpackCompose(this)

    defaultConfig {
        applicationId = "com.github.terrakok.androidcomposeapp"
        targetSdk = libs.versions.compileSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    implementation(projects.modoCompose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.ui)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.lifecycle.runtimeKtx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.compose.material)

    implementation(libs.debug.logcat)

    debugImplementation("com.squareup.leakcanary:leakcanary-android:2.14")
}