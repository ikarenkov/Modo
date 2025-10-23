import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "com.github.terrakok.modo.buildlogic"

// Configure the build-logic plugins to target JDK 17
// This matches the JDK used to build the project, and is not related to what is running on device.
java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}
tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    compileOnly(libs.android.gradlePlugin)
    compileOnly(libs.android.tools.common)
    compileOnly(libs.kotlin.gradlePlugin)
    compileOnly(libs.detektPlugin)
    compileOnly(libs.compose.compile.gradlePlugin)
    // workaround for https://github.com/gradle/gradle/issues/15383
    compileOnly(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}

gradlePlugin {
    plugins {
        register("modo-detekt") {
            id = "modo-detekt"
            implementationClass = "com.github.terrakok.DetektPlugin"
        }
        register("modo-android-library") {
            id = "modo-android-library"
            implementationClass = "com.github.terrakok.AndroidLibraryPlugin"
        }
        register("modo-android-app") {
            id = "modo-android-app"
            implementationClass = "com.github.terrakok.AndroidAppPlugin"
        }
        register("modo-jetpack-compose-library") {
            id = "modo-jetpack-compose-library"
            implementationClass = "com.github.terrakok.JetpackComposeLibraryPlugin"
        }
        register("modo-jetpack-compose-app") {
            id = "modo-jetpack-compose-app"
            implementationClass = "com.github.terrakok.JetpackComposeAppPlugin"
        }
        register("modo-publishing") {
            id = "modo-publishing"
            implementationClass = "com.github.terrakok.PublishingPlugin"
        }
        register("modo-collect-sarif") {
            id = "modo-collect-sarif"
            implementationClass = "com.github.terrakok.CollectSarifPlugin"
        }
    }
}
