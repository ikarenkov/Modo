import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
        classpath("com.android.tools.build:gradle:${libs.versions.androidGradlePlugin.get()}")
    }
}

allprojects {
    group = "com.github.terrakok"
    version = "0.7.2-dev2"

    //PUBLISHING './gradlew clean bundleReleaseAar publishAllPublicationsToSonatypeRepository'
    val localProps = gradleLocalProperties(rootDir)
    val emptyJavadocJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
    }

    afterEvaluate {
        extensions.findByType<PublishingExtension>()?.apply {
            publications.withType<MavenPublication>().configureEach {
                artifact(emptyJavadocJar.get())

                pom {
                    name = "Modo"
                    description = "Navigation library for Jetpack Compose based on UDF principles"
                    url = "https://github.com/terrakok/Modo"

                    licenses {
                        license {
                            name = "MIT"
                            url = "https://opensource.org/licenses/MIT"
                        }
                    }
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
                    scm {
                        url = "https://github.com/terrakok/Modo"
                    }
                }
            }

            repositories {
                maven {
                    name = "sonatype"
                    url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
                    credentials {
                        username = localProps.getProperty("ossrhUsername")
                        password = localProps.getProperty("ossrhPassword")
                    }
                }
            }
        }

        project.ext["signing.keyId"] = localProps.getProperty("signing.keyId")
        project.ext["signing.secretKeyRingFile"] = localProps.getProperty("signing.secretKeyRingFile")
        project.ext["signing.password"] = localProps.getProperty("signing.password")
        extensions.findByType<SigningExtension>()?.apply {
            val publishing = extensions.findByType<PublishingExtension>() ?: return@apply
            sign(publishing.publications)
        }

        val isReleaseBuild = localProps.containsKey("signing.keyId")
        tasks.withType<Sign>().configureEach {
            onlyIf { isReleaseBuild }
        }
    }
}