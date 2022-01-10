plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
    id("signing")
}

kotlin {
    android {
        publishAllLibraryVariants()
    }
    jvm() //for future experiments
    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
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