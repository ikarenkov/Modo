import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.4.30")
        classpath("com.android.tools.build:gradle:7.0.0-alpha09")
    }
}

allprojects {
    group = "com.github.terrakok"
    version = "0.6"

    repositories {
        google()
        mavenCentral()
    }

    //PUBLISHING './gradlew bundleReleaseAar publishAllPublicationsToSonatypeRepository'
    val localProps = gradleLocalProperties(rootDir)
    val emptyJavadocJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
    }

    afterEvaluate {
        extensions.findByType<PublishingExtension>()?.apply {
            publications.withType<MavenPublication>().configureEach {
                artifact(emptyJavadocJar.get())

                pom {
                    name.set("Modo")
                    description.set("Navigation library based on UDF principles")
                    url.set("https://github.com/terrakok/Modo")

                    licenses {
                        license {
                            name.set("MIT")
                            url.set("https://opensource.org/licenses/MIT")
                        }
                    }
                    developers {
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