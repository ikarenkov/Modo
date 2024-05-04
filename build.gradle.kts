plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.parcelize) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.modo.android.library) apply false
    alias(libs.plugins.modo.android.app) apply false
    alias(libs.plugins.modo.publishing) apply false
    alias(libs.plugins.modo.detekt)
    alias(libs.plugins.modo.collectSarif)
}

tasks.named<Wrapper>("wrapper") {
    distributionType = Wrapper.DistributionType.ALL
    gradleVersion = "8.7"
}

// PUBLISHING './gradlew clean modo-compose:bundleReleaseAar modo-compose:publishAllPublicationsToSonatypeRepository'