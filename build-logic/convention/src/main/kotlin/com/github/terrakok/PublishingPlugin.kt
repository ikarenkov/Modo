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
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.plugins.signing.Sign
import org.gradle.plugins.signing.SigningExtension
import java.util.Properties

// PUBLISHING './gradlew clean bundleReleaseAar publishAllPublicationsToSonatypeRepository'
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
        // Configure maven central repository
        repositories {
            maven("https://oss.sonatype.org/service/local/staging/deploy/maven2/") {
                name = "sonatype"
                credentials {
                    username = getExtraString("ossrhUsername")
                    password = getExtraString("ossrhPassword")
                }
            }
        }
        // Configure all publications
        publications.register<MavenPublication>("release") {
            groupId = publicationGroupId
            version = publicationVersion

            // Provide artifacts information requited by Maven Central
            pom {
                name = "Modo"
                description = "Navigation library for Jetpack Compose based on UDF principles"
                url = "https://github.com/terrakok/Modo"
                scm {
                    url = "https://github.com/terrakok/Modo"
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
    extra["signing.keyId"] = null
    extra["signing.password"] = null
    extra["signing.secretKeyRingFile"] = null
    extra["sonatypeUsername"] = null
    extra["sonatypePassword"] = null

// Grabbing secrets from local.properties file or from environment variables, which could be used on CI
    val secretPropsFile = project.rootProject.file("local.properties")
    if (secretPropsFile.exists()) {
        secretPropsFile.reader().use {
            Properties().apply { load(it) }
        }.onEach { (name, value) ->
            extra[name.toString()] = value
        }
        extra["signing.secretKeyRingFile"] = project.rootProject.layout.projectDirectory.file(extra["signing.secretKeyRingFile"].toString())
    } else {
        extra["signing.keyId"] = System.getenv("SIGNING_KEY_ID")
        extra["signing.password"] = System.getenv("SIGNING_PASSWORD")
        extra["signing.secretKeyRingFile"] = System.getenv("SIGNING_SECRET_KEY_RING_FILE")
        extra["sonatypeUsername"] = System.getenv("SONATYPE_USERNAME")
        extra["sonatypePassword"] = System.getenv("SONATYPE_PASSWORD")
    }
}

private fun Project.getExtraString(name: String): String? = if (extra.has(name)) extra[name]?.toString() else null