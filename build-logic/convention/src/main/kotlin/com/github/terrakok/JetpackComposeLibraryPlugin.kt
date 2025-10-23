package com.github.terrakok

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

class JetpackComposeLibraryPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            withVersionCatalog {
                pluginManager.apply(libs.plugins.compose.compiler.get().pluginId)
            }
            configureJetpackCompose(extensions.getByType<LibraryExtension>())
        }
    }
}