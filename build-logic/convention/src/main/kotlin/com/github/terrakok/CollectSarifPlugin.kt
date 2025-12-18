package com.github.terrakok

import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.gradle.api.DefaultTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.register

class CollectSarifPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            val fixLintSarifTask = tasks.register<FixLintSarifTask>(FIX_LINT_SARIF) {
                group = JavaBasePlugin.VERIFICATION_GROUP
                outputDir.set(layout.buildDirectory.dir("reports/lint-sarif-fixed"))
            }

            tasks.register<ReportMergeTask>(MERGE_LINT_SARIF) {
                group = JavaBasePlugin.VERIFICATION_GROUP
                output.set(layout.buildDirectory.file("reports/lint-merged.sarif"))
                dependsOn(fixLintSarifTask)
                // Configure inputs from the fix task's output directory
                input.from(
                    fixLintSarifTask.map { task ->
                        task.outputDir.get().asFileTree.matching {
                            include("**/*.sarif")
                        }
                    }
                )
            }
        }
    }

    companion object {
        const val MERGE_LINT_SARIF = "mergeLintSarif"
        const val FIX_LINT_SARIF = "fixLintSarif"
    }
}

abstract class FixLintSarifTask : DefaultTask() {

    @get:InputFiles
    abstract val inputFiles: ConfigurableFileCollection

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @TaskAction
    fun fixSarifFiles() {
        val outputDirectory = outputDir.get().asFile
        outputDirectory.mkdirs()

        // Group input files by their parent project to avoid name collisions
        val filesByProject = inputFiles.files.groupBy { file ->
            // Extract project path from file path
            // e.g., /path/to/project/modo-compose/build/reports/lint-results-debug.sarif -> modo-compose
            val buildIndex = file.absolutePath.indexOf("/build/")
            if (buildIndex > 0) {
                val projectPath = file.absolutePath.substring(0, buildIndex)
                projectPath.substringAfterLast("/")
            } else {
                "unknown"
            }
        }

        filesByProject.forEach { (projectName, files) ->
            files.forEach { inputFile ->
                if (inputFile.exists() && inputFile.extension == "sarif") {
                    val content = inputFile.readText()
                    // Fix trailing commas in arrays and objects which are invalid JSON
                    val fixedContent = content
                        .replace(Regex(",\\s*]"), "]")  // Remove trailing comma before ]
                        .replace(Regex(",\\s*}"), "}")  // Remove trailing comma before }

                    // Create unique output filename using project name
                    val outputFileName = "${projectName}-${inputFile.name}"
                    val outputFile = outputDirectory.resolve(outputFileName)
                    outputFile.writeText(fixedContent)
                    logger.lifecycle("Fixed SARIF file: ${inputFile.absolutePath} -> ${outputFile.absolutePath}")
                }
            }
        }
    }
}