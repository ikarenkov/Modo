import com.github.terrakok.configureJetpackCompose
import com.github.terrakok.configureKotlinAndroid

plugins {
    alias(libs.plugins.modo.android.app)
    alias(libs.plugins.kotlin.parcelize)
}

android {
    namespace = "com.github.terrakok.modo.sample"

    configureKotlinAndroid(this)
    configureJetpackCompose(this)

    defaultConfig {
        applicationId = "com.github.terrakok.modo.sample"
        targetSdk = libs.versions.compileSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom.app))
    androidTestImplementation(platform(libs.androidx.compose.bom.app))

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

    implementation(libs.androidx.compose.material)
    implementation(libs.androidx.compose.material3)

    implementation(libs.debug.logcat)
    implementation(libs.kotlinx.coroutines.android)

    debugImplementation(libs.leakcanary.android)
}