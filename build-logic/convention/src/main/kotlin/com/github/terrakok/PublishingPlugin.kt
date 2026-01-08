@file:Suppress("NoUnusedImports")

package com.github.terrakok

import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.publish.maven.tasks.AbstractPublishToMaven
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import java.util.Properties

/**
 * Configures Maven publishing for library modules.
 *
 * Sets up:
 * - AAR, sources, and javadoc artifacts
 * - POM metadata (name, description, developers, licenses)
 * - GPG artifact signing
 *
 * Repository configuration is in root build.gradle.kts (nexus-publish plugin).
 * See PUBLISHING.md for publishing instructions.
 */
class PublishingPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            with(pluginManager) {
                apply("maven-publish")
                apply("signing")
            }
            setupPublishing()
        }
    }
}

internal fun Project.setupPublishing() {
    // For generation everething that we need for publishing: aar, sources, docs, etc.
    configure<LibraryExtension> {
        publishing {
            singleVariant("release") {
                withJavadocJar()
                withSourcesJar()
            }
        }
    }

    // configure publishing repo, credentials, version, group
    configurePublishingToSunatype(
        publicationGroupId = "com.github.terrakok",
        publicationVersion = getFromVersionCatalog { libs.versions.modo.get() }!!
    )
}

private fun Project.configurePublishingToSunatype(
    publicationVersion: String,
    publicationGroupId: String
) {
    readEnvironmentVariables()

    val isSigningEnabled = getExtraString("signing.keyId") != null

    if (isSigningEnabled) {
        // Workaround for https://github.com/gradle/gradle/issues/26091 and https://youtrack.jetbrains.com/issue/KT-46466
        val signingTasks = tasks.withType<Sign>()
        tasks.withType<AbstractPublishToMaven>().configureEach {
            dependsOn(signingTasks)
        }
    }

    configure<PublishingExtension> {
        // Repository configuration is handled by nexus-publish plugin in root build.gradle.kts
        // This plugin only configures the publication artifacts and metadata
        // Configure all publications
        publications.register<MavenPublication>("release") {
            groupId = publicationGroupId
            version = publicationVersion

            // Provide artifacts information requited by Maven Central
            pom {
                name = "Modo"
                description = "Navigation library for Jetpack Compose based on UDF principles"
                url = "https://github.com/ikarenkov/Modo"
                scm {
                    url = "https://github.com/ikarenkov/Modo"
                }
                setupLicense()
                setupDevelopers()
            }
            afterEvaluate {
                // provide what to publish, we created it before
                from(components["release"])
            }
        }
        if (isSigningEnabled) {
            configure<SigningExtension> {
                // Signing artifacts. Signing.* extra properties values will be used
                sign(publications)
            }
        }
    }
}

private fun MavenPom.setupDevelopers() {
    developers {
        developer {
            id = "ikarenkov"
            name = "Igor Karenkov"
            email = "karenkovigor@gmail.com"
        }
        developer {
            id = "terrakok"
            name = "Konstantin Tskhovrebov"
            email = "terrakok@gmail.com"
        }
    }
}

private fun MavenPom.setupLicense() {
    licenses {
        license {
            name.set("MIT")
            url.set("https://opensource.org/licenses/MIT")
        }
    }
}

private fun Project.readEnvironmentVariables() {
    // Read credentials from local.properties or environment variables
    // Set them on rootProject.extra so they're accessible from root build.gradle.kts
    rootProject.extra["signing.keyId"] = null
    rootProject.extra["signing.password"] = null
    rootProject.extra["signing.secretKeyRingFile"] = null
    rootProject.extra["sonatypeUsername"] = null
    rootProject.extra["sonatypePassword"] = null

    val secretPropsFile = project.rootProject.file("local.properties")
    if (secretPropsFile.exists()) {
        // Read all properties from local.properties
        secretPropsFile.reader().use {
            Properties().apply { load(it) }
        }.onEach { (name, value) ->
            rootProject.extra[name.toString()] = value
        }
        // Convert relative path to absolute for signing key file
        if (rootProject.extra.has("signing.secretKeyRingFile")) {
            rootProject.extra["signing.secretKeyRingFile"] = project.rootProject.layout.projectDirectory
                .file(rootProject.extra["signing.secretKeyRingFile"].toString())
        }
    } else {
        // Read from environment variables (for CI)
        rootProject.extra["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
        rootProject.extra["signing.password"] = System.getenv("SIGNING_PASSWORD")
        rootProject.extra["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
        rootProject.extra["sonatypeUsername"] = System.getenv("SONATYPE_USERNAME")
        rootProject.extra["sonatypePassword"] = System.getenv("SONATYPE_PASSWORD")
    }
}

private fun Project.getExtraString(name: String): String? = if (rootProject.extra.has(name)) rootProject.extra[name]?.toString() else null