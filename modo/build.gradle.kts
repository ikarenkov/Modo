import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
    id("maven-publish")
    id("signing")
}

kotlin {
    android {
        publishAllLibraryVariants()
    }
    jvm()
    iosX64()
    iosArm64()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(compose("org.jetbrains.compose.runtime:runtime"))
                implementation(compose("org.jetbrains.compose.runtime:runtime-saveable"))
                implementation(compose("org.jetbrains.compose.animation:animation"))
            }
        }
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
        }

        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
        }
    }
}

android {
    namespace = "com.github.terrakok.modo"
    compileSdk = (properties["android.compileSdk"] as String).toInt()
    defaultConfig {
        minSdk = (properties["android.minSdk"] as String).toInt()
        targetSdk = (properties["android.targetSdk"] as String).toInt()
    }
}