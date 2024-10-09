package io.github.ikarenkov.workshop.screens.profile_setup

import io.github.ikarenkov.workshop.domain.ClimbingType
import io.github.ikarenkov.workshop.screens.TrainingRecommendationsScreen
import io.github.ikarenkov.workshop.screens.climbing_level.ClimbingLevelScreen

fun getNextProfileSetupStepScreen(step: Int): SetupStepScreen? = when (step) {
        1 -> ClimbingLevelScreen(ClimbingType.Sport)
        2 -> ClimbingLevelScreen(ClimbingType.Bouldering)
        3 -> TrainingRecommendationsScreen()
        else -> null
}