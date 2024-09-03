import com.github.terrakok.configureJetpackCompose
import com.github.terrakok.configureKotlinAndroid

plugins {
    alias(libs.plugins.modo.android.app)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "io.github.ikarenkov.workshop"

    configureKotlinAndroid(this)
    configureJetpackCompose(this)

    defaultConfig {
        applicationId = "io.github.ikarenkov.workshopapp"
        targetSdk = libs.versions.compileSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom.app))
    androidTestImplementation(platform(libs.androidx.compose.bom.app))

    // TODO: Workshop 1.1 - Add dependencies for modo
    implementation(projects.modoCompose)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.compose.ui)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.foundation.beta)
    implementation(libs.androidx.lifecycle.runtimeKtx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.fragment)
    implementation("com.github.zj565061763:compose-wheel-picker:1.0.0-beta05")
    implementation(libs.koin.android)
    implementation(libs.koin.compose)

    implementation(libs.androidx.compose.material3)

    implementation(libs.debug.logcat)
    implementation(libs.kotlinx.coroutines.android)

    debugImplementation(libs.leakcanary.android)
}