plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
    id("signing")
    id("kotlin-parcelize")
}

kotlin {
    android {
        publishAllLibraryVariants()
    }
    // for future experiments with compose multiplatform
    linuxX64()
    jvm()
    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}

android {
    compileSdk = (properties["android.compileSdk"] as String).toInt()
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = (properties["android.minSdk"] as String).toInt()
        targetSdk = (properties["android.targetSdk"] as String).toInt()
    }
}

dependencies {
    commonMainApi("dev.icerock.moko:parcelize:0.8.0")
}