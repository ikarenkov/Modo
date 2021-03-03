pluginManagement {
    repositories {
        google()
        jcenter()
        gradlePluginPortal()
        mavenCentral()
    }
    
}
rootProject.name = "Modo"

include(":androidApp")
include(":modo")
include(":modo-android-classic")
include(":modo-android-compose")
include(":androidComposeApp")
