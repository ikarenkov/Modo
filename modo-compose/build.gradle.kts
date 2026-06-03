plugins {
    alias(libs.plugins.modo.android.library)
    alias(libs.plugins.modo.compose.library)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.modo.publishing)
    alias(libs.plugins.dependencyGuard)
}

dependencyGuard {
    configuration("debugCompileClasspath")
    configuration("debugRuntimeClasspath")
}

android {
    namespace = "com.github.terrakok.modo.android.compose"

    testOptions {
        unitTests {
            isReturnDefaultValues = true
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom.modo))
    androidTestImplementation(platform(libs.androidx.compose.bom.modo))

    implementation(libs.androidx.fragment)

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.animation)
    // For BackHandler
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
//    implementation("org.jetbrains.kotlin:kotlin-parcelize-runtime:${properties["version.kotlin"]}")

    testImplementation(libs.test.junit.jupiter)
    testImplementation(kotlin("test"))
    testImplementation(libs.test.androidx.arch.core)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
}

tasks.withType(Test::class) {
    useJUnitPlatform()
    reports {
        junitXml.required = true
    }
}