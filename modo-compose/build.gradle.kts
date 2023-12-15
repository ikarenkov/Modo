import com.github.terrakok.configureKotlinAndroid
import com.github.terrakok.configureJetpackCompose

plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
    id("maven-publish")
    id("signing")
    id("build-logic")
}

android {
    namespace = "com.github.terrakok.modo.android.compose"

    configureKotlinAndroid(this)
    configureJetpackCompose(this)
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(platform(libs.androidx.compose.bom))

    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.animation)
    // For BackHandler
    implementation(libs.androidx.activity.compose)
//    implementation("org.jetbrains.kotlin:kotlin-parcelize-runtime:${properties["version.kotlin"]}")

    testImplementation(libs.test.junit.jupiter)
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