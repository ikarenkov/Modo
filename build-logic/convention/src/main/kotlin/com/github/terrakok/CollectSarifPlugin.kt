package com.github.terrakok

import com.android.build.gradle.internal.lint.AndroidLintTask
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType

class CollectSarifPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        with(target) {
            tasks.register<ReportMergeTask>(MERGE_LINT_SARIF) {
                group = JavaBasePlugin.VERIFICATION_GROUP
                output.set(layout.buildDirectory.file("reports/lint-merged.sarif"))
            }
        }
    }

    companion object {
        const val MERGE_LINT_SARIF = "mergeLintSarif"
    }

}