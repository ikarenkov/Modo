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
        reports {
            sarif.required = true
        }
        // The directories where detekt looks for source files.
        // Defaults to `files("src/main/java", "src/test/java", "src/main/kotlin", "src/test/kotlin")`.
        setSource(projectDir)

        include("**/*.kt")
        include("**/*.kts")
        exclude("**/resources/")
        exclude("**/build/")
        exclude("Writerside/codeSnippets/")

        // Builds the AST in parallel. Rules are always executed in parallel.
        // Can lead to speedups in larger projects. `false` by default.
        parallel = true

        // Define the detekt configuration(s) you want to use.
        // Defaults to the default detekt configuration.
        config.setFrom("config/detekt/detekt.yml")

        // Applies the config files on top of detekt's default config file. `false` by default.
        buildUponDefaultConfig = false

        // Turns on all the rules. `false` by default.
        allRules = false

        // Specifying a baseline file. All findings stored in this file in subsequent runs of detekt.
//    baseline = file("path/to/baseline.xml")

        // Disables all default detekt rulesets and will only run detekt with custom rules
        // defined in plugins passed in with `detektPlugins` configuration. `false` by default.
        disableDefaultRuleSets = false

        // Adds debug output during task execution. `false` by default.
        debug = false

        // If set to `true` the build does not fail when the
        // maxIssues count was reached. Defaults to `false`.
        ignoreFailures = true

        // Specify the base path for file paths in the formatted reports.
        // If not set, all file paths reported will be absolute file path.
        basePath = rootProject.projectDir.absolutePath
    }
}