import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${properties["version.kotlin"]}")
        classpath("org.jetbrains.compose:compose-gradle-plugin:${properties["version.composeGradle"]}")
        classpath("com.android.tools.build:gradle:${properties["version.agp"]}")
    }
}

allprojects {
    group = "com.github.terrakok"
    version = "1.0.0"

    repositories {
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }

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