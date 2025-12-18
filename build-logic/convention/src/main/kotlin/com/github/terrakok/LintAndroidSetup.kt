package com.github.terrakok

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.internal.lint.AndroidLintTask
import org.gradle.api.Project

fun Project.configureLintAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.lint {
        abortOnError = false
        warningsAsErrors = true
        checkReleaseBuilds = false
        sarifReport = true
        checkAllWarnings = true
        htmlReport = true
        lintConfig = rootProject.file("config/lint/lint.xml")
    }

    // Use afterEvaluate to ensure the lintReportDebug task exists
    afterEvaluate {
        tasks.matching { it.name == "lintReportDebug" && it is AndroidLintTask }.configureEach {
            val lintTask = this as AndroidLintTask
            val lintSarifFile = lintTask.sarifReportOutputFile

            // Add lint SARIF files to the fix task
            rootProject.tasks.named(
                CollectSarifPlugin.FIX_LINT_SARIF,
                FixLintSarifTask::class.java,
            ) {
                inputFiles.from(lintSarifFile)
            }
        }
    }
}