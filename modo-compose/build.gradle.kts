import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties
import com.github.terrakok.configureJetpackCompose
import com.github.terrakok.configureKotlinAndroid

plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
    id("maven-publish")
    id("signing")
    id("build-logic")
}

group = "com.github.terrakok"
version = "0.7.2-dev2"

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

//PUBLISHING './gradlew clean bundleReleaseAar publishAllPublicationsToSonatypeRepository'
val localProps = gradleLocalProperties(rootDir)
val emptyJavadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

publishing {
    publications {
        create<MavenPublication>("release") {
            artifact(sourceJar.get())
            artifact("${layout.buildDirectory.get()}/outputs/aar/${artifactId}-${name}.aar")

            artifact(emptyJavadocJar.get())

            pom {
                name = "Modo"
                description = "Navigation library for Jetpack Compose based on UDF principles"
                url = "https://github.com/terrakok/Modo"

                licenses {
                    license {
                        name = "MIT"
                        url = "https://opensource.org/licenses/MIT"
                    }
                }
                developers {
                    developer {
                        id = "ikarenkov"
                        name = "Igor Karenkov"
                        email = "karenkovigor@gmail.com"
                    }
                    developer {
                        id = "terrakok"
                        name = "Konstantin Tskhovrebov"
                        email = "terrakok@gmail.com"
                    }
                }
                scm {
                    url = "https://github.com/terrakok/Modo"
                }
            }
        }
    }

    repositories {
        maven {
            name = "sonatype"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = localProps.getProperty("ossrhUsername")
                password = localProps.getProperty("ossrhPassword")
            }
        }
    }
}

signing {
    sign(publishing.publications)
}

val isReleaseBuild = localProps.containsKey("signing.keyId")
tasks.withType<Sign>().configureEach {
    onlyIf { isReleaseBuild }
}