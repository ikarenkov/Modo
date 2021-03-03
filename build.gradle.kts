buildscript {
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.30")
        classpath("com.android.tools.build:gradle:7.0.0-alpha08")
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
}