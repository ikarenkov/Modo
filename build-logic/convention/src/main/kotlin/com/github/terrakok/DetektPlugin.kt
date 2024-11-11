@file:Suppress("NoUnusedImports")

package com.github.terrakok

import io.gitlab.arturbosch.detekt.Detekt
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.register

class DetektPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            withVersionCatalog {
                pluginManager.apply(libs.plugins.detekt.get().pluginId)
                setupDetektTask()
                dependencies {
                    add("detektPlugins", libs.detekt.composeRules)
                    add("detektPlugins", libs.detekt.formatting)
                }
            }
        }
    }
}

fun Project.setupDetektTask() {
    tasks.register<Detekt>("detektAll") {
        configureDetekt(this)
    }
    tasks.register<Detekt>("detektFormat") {
        configureDetekt(this)
        autoCorrect = true
    }
}

private fun Project.configureDetekt(detekt: Detekt) {
    detekt.reports {
        sarif.required = true
    }
    // The directories where detekt looks for source files.
    // Defaults to `files("src/main/java", "src/test/java", "src/main/kotlin", "src/test/kotlin")`.
    detekt.setSource(project.projectDir)

    detekt.include("**/*.kt")
    detekt.include("**/*.kts")
    detekt.exclude("**/resources/")
    detekt.exclude("**/build/")
    detekt.exclude("Writerside/codeSnippets/")

    // Builds the AST in parallel. Rules are always executed in parallel.
    // Can lead to speedups in larger projects. `false` by default.
    detekt.parallel = true

    // Define the detekt configuration(s) you want to use.
    // Defaults to the default detekt configuration.
    detekt.config.setFrom("config/detekt/detekt.yml")

    // Applies the config files on top of detekt's default config file. `false` by default.
    detekt.buildUponDefaultConfig = false

    // Turns on all the rules. `false` by default.
    detekt.allRules = false

    // Specifying a baseline file. All findings stored in this file in subsequent runs of detekt.
//    baseline = file("path/to/baseline.xml")

    // Disables all default detekt rulesets and will only run detekt with custom rules
    // defined in plugins passed in with `detektPlugins` configuration. `false` by default.
    detekt.disableDefaultRuleSets = false

    // Adds debug output during task execution. `false` by default.
    detekt.debug = false

    // If set to `true` the build does not fail when the
    // maxIssues count was reached. Defaults to `false`.
    detekt.ignoreFailures = true

    // Specify the base path for file paths in the formatted reports.
    // If not set, all file paths reported will be absolute file path.
    detekt.basePath = project.rootProject.projectDir.absolutePath
}