plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
    id("maven-publish")
    id("signing")
}

android {
    namespace = "com.github.terrakok.modo.android.compose"
    compileSdk = (properties["android.compileSdk"] as String).toInt()

    defaultConfig {
        minSdk = (properties["android.minSdk"] as String).toInt()
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

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.animation:animation")
    // For BackHandler
    implementation("androidx.activity:activity-compose:${properties["version.composeActivity"]}")
    implementation("org.jetbrains.kotlin:kotlin-parcelize-runtime:${properties["version.kotlin"]}")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

tasks.withType(Test::class) {
    useJUnitPlatform()
}

val sourceJar by tasks.registering(Jar::class) {
    from(android.sourceSets["main"].java.srcDirs().srcDirs)
    archiveClassifier.set("sources")
}

publishing {
    publications {
        create<MavenPublication>("release") {
            artifact(sourceJar.get())
            artifact("${layout.buildDirectory}/outputs/aar/${artifactId}-${name}.aar")
        }
    }
}