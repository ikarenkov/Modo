plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.dependencyGuard) apply false
    alias(libs.plugins.modo.android.library) apply false
    alias(libs.plugins.modo.android.app) apply false
    alias(libs.plugins.modo.publishing) apply false
    alias(libs.plugins.modo.detekt)
    alias(libs.plugins.modo.collectSarif)
    alias(libs.plugins.nexus.publish)
}

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "9.1.0"
}

// Read credentials from local.properties for nexus-publish plugin
val localProperties = file("local.properties").takeIf { it.exists() }?.let {
    java.util.Properties().apply { load(it.inputStream()) }
}

nexusPublishing {
    repositories {
        sonatype {
            // OSSRH Staging API compatibility endpoint for Central Portal
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))

            // Use Central Portal credentials from local.properties or environment
            username.set(
                providers
                    .environmentVariable("SONATYPE_USERNAME")
                    .orElse(provider { localProperties?.getProperty("sonatypeUsername") ?: "" })
            )
            password.set(
                providers
                    .environmentVariable("SONATYPE_PASSWORD")
                    .orElse(provider { localProperties?.getProperty("sonatypePassword") ?: "" })
            )
        }
    }

    this.packageGroup.set("com.github.terrakok")
}

// Publishing configuration for Central Portal (via OSSRH Staging API compatibility endpoint)
// See PUBLISHING.md for detailed publishing instructions