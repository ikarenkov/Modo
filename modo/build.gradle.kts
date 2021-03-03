import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

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
    compileSdkVersion(30)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
    }
}

//PUBLISHING
// ./gradlew clean modo:publishAllPublicationsToSonatypeRepository

val localProps = gradleLocalProperties(rootDir)

group = "com.github.terrakok"
version = "0.5"

val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

publishing {
    publications.withType<MavenPublication>().configureEach {
        artifact(javadocJar.get())

        pom {
            name.set("Modo")
            description.set("Navigation library based on UDF principles")
            url.set("https://github.com/terrakok/Modo")

            licenses {
                license {
                    name.set("MIT")
                    url.set("https://opensource.org/licenses/MIT")
                }
            }
            developers {
                developer {
                    id.set("terrakok")
                    name.set("Konstantin Tskhovrebov")
                    email.set("terrakok@gmail.com")
                }
            }
            scm {
                url.set("https://github.com/terrakok/Modo")
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

project.ext["signing.keyId"] = localProps.getProperty("signing.keyId")
project.ext["signing.secretKeyRingFile"] = localProps.getProperty("signing.secretKeyRingFile")
project.ext["signing.password"] = localProps.getProperty("signing.password")
signing {
    sign(publishing.publications)
}