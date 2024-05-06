package com.github.terrakok

import com.android.build.api.dsl.CommonExtension
import com.android.build.gradle.internal.lint.AndroidLintTask
import io.gitlab.arturbosch.detekt.report.ReportMergeTask
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
    rootProject.tasks.named(
        CollectSarifPlugin.MERGE_LINT_SARIF,
        ReportMergeTask::class.java,
    ) {
        input.from(
            tasks
                .named("lintReportDebug", AndroidLintTask::class.java)
                .flatMap { it.sarifReportOutputFile }
        )
    }
}