package com.github.terrakok

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class AndroidAppPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            withVersionCatalog {
                with(pluginManager) {
                    apply(libs.plugins.android.application.get().pluginId)
                    apply(libs.plugins.kotlin.android.get().pluginId)
                }
            }
            val android = extensions.getByType<ApplicationExtension>()
            configureKotlinAndroid(android)
            configureLintAndroid(android)
        }
    }
}