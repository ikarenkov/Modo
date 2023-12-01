import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${properties["version.kotlin"]}")
        classpath("com.android.tools.build:gradle:8.2.0")
    }
}

allprojects {
    group = "com.github.terrakok"
    version = "0.7.2-dev2"

    repositories {
        google()
        mavenCentral()
    }

    //PUBLISHING './gradlew clean bundleReleaseAar publishAllPublicationsToSonatypeRepository'
    val localProps = gradleLocalProperties(rootDir)
    val emptyJavadocJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
    }

    configurations.all {
        // fix for new compose with old kotlin version
        resolutionStrategy.force("org.jetbrains.kotlin:kotlin-stdlib:${properties["version.kotlin"]}")
    }
    afterEvaluate {
        extensions.findByType<PublishingExtension>()?.apply {
            publications.withType<MavenPublication>().configureEach {
                artifact(emptyJavadocJar.get())

                pom {
                    name.set("Modo")
                    description.set("Navigation library for Jetpack Compose based on UDF principles")
                    url.set("https://github.com/terrakok/Modo")

                    licenses {
                        license {
                            name.set("MIT")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    developers {
                        developer {
                            id.set("ikarenkov")
                            name.set("Igor Karenkov")
                            email.set("karenkovigor@gmail.com")
                        }
                        developer {
                            id.set("terrakok")
                            name.set("Konstantin Tskhovrebov")
                            email.set("terrakok@gmail.com")
                        }
                    }
                    scm {
                        url.set("https://github.com/terrakok/Modo")
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