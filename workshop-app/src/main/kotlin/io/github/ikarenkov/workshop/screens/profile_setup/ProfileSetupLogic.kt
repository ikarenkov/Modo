package io.github.ikarenkov.workshop.screens.profile_setup

import io.github.ikarenkov.workshop.domain.ClimberProfile
import io.github.ikarenkov.workshop.domain.ClimbingType
import io.github.ikarenkov.workshop.screens.TrainingRecommendationsScreen
import io.github.ikarenkov.workshop.screens.climbing_level.ClimbingLevelScreen
import io.github.ikarenkov.workshop.screens.personal_data.ClimberPersonalInfoScreenFinal

@Suppress("MagicNumber")
fun getNextProfileSetupStepScreen(step: Int): SetupStepScreen? = when (step) {
        1 -> ClimbingLevelScreen(ClimbingType.Sport)
        2 -> ClimbingLevelScreen(ClimbingType.Bouldering)
        3 -> TrainingRecommendationsScreen()
        else -> null
}

@Suppress("MagicNumber")
fun getProfileSetupInitialScreens(step: Int) = listOfNotNull(
    ClimberPersonalInfoScreenFinal(),
    if (step >= 2) ClimbingLevelScreen(ClimbingType.Sport) else null,
    if (step >= 3) ClimbingLevelScreen(ClimbingType.Bouldering) else null,
    if (step >= 4) TrainingRecommendationsScreen() else null
)

@Suppress("MagicNumber")
fun getProfileSetupStartingStep(profile: ClimberProfile) = when {
    profile.boulderLevel.hasAllGrades() -> 4
    profile.sportLevel.hasAllGrades() -> 3
    profile.dateOfBirth != null && profile.heightSm != null && profile.weightKg != null -> 2
    else -> 1
}