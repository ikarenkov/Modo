package io.github.ikarenkov.workshop.domain

import java.util.Date

data class ClimberProfile(
    val name: String? = "Igor Karenkov",
    val description: String? = "Software engineer, climber, and digital nomad.",
    val dateOfBirth: Date? = null,
    val heightSm: Int? = null,
    val weightKg: Float? = null,
    val sportLevel: ClimbingLevel = ClimbingLevel(),
    val boulderLevel: ClimbingLevel = ClimbingLevel(),
)

data class ClimbingLevel(
    val redpointGrade: FrenchScaleGrade? = null,
    val onsightGrade: FrenchScaleGrade? = null,
    val flashGrade: FrenchScaleGrade? = null,
) {
    fun hasAnyGrade(): Boolean = redpointGrade != null || onsightGrade != null || flashGrade != null
    fun hasAllGrades(): Boolean = redpointGrade != null && onsightGrade != null && flashGrade != null
}