plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
    id("signing")
}

android {
    compileSdk = 30

    defaultConfig {
        minSdk = 21
        targetSdk = 30
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(project(":modo"))
    implementation("androidx.fragment:fragment:1.3.3")

    testImplementation("junit:junit:4.13.2")
}

val sourceJar by tasks.registering(Jar::class) {
    from(android.sourceSets["main"].java.srcDirs())
    archiveClassifier.set("sources")
}

publishing {
    publications {
        create<MavenPublication>("release") {
            artifact(sourceJar.get())
            artifact("$buildDir/outputs/aar/${artifactId}-${name}.aar")
        }
    }
}