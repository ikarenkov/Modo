package com.github.terrakok

import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Project
import org.gradle.kotlin.dsl.the

/**
 * workaround to make version catalog accessible in convention plugins
 * https://github.com/gradle/gradle/issues/15383
 */
fun Project.withVersionCatalog(block: VersionCatalogueScope.() -> Unit) {
    if (project.name != "gradle-kotlin-dsl-accessors") {
        val libs = the<LibrariesForLibs>()
        block.invoke(VersionCatalogueScope(libs))
    }
}

/**
 * workaround to make version catalog accessible in convention plugins
 * https://github.com/gradle/gradle/issues/15383
 */
fun <T> Project.getFromVersionCatalog(block: VersionCatalogueScope.() -> T): T? =
    if (project.name != "gradle-kotlin-dsl-accessors") {
        val libs = the<LibrariesForLibs>()
        block.invoke(VersionCatalogueScope(libs))
    } else {
        null
    }

class VersionCatalogueScope(
    val libs: LibrariesForLibs
)