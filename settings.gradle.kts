pluginManagement {
    includeBuild("build-logic")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

}
rootProject.name = "Modo"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

//include(":modo")
include(":modo-compose")
include(":sample")
