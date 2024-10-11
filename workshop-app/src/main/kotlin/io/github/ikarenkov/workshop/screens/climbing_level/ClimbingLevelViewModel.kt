package io.github.ikarenkov.workshop.screens.climbing_level

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.ikarenkov.workshop.data.ClimberProfileRepository
import io.github.ikarenkov.workshop.domain.ClimbingLevel
import io.github.ikarenkov.workshop.domain.ClimbingType
import io.github.ikarenkov.workshop.domain.FrenchScaleGrade
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ClimbingLevelViewModel(
    private val type: ClimbingType,
    private val profileRepository: ClimberProfileRepository
) : ViewModel() {
    val state: StateFlow<ClimbingLevel> =
        profileRepository.climberProfile
            .map { profile ->
                when (type) {
                    ClimbingType.Sport -> profile.sportLevel
                    ClimbingType.Bouldering -> profile.boulderLevel
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.Lazily,
                initialValue = profileRepository.climberProfile.value.sportLevel
            )

    fun setRedpointGrade(grade: FrenchScaleGrade) {
        profileRepository.updateClimberProfile {
            when (type) {
                ClimbingType.Sport -> copy(sportLevel = sportLevel.copy(redpointGrade = grade))
                ClimbingType.Bouldering -> copy(boulderLevel = boulderLevel.copy(redpointGrade = grade))
            }
        }
    }

    fun setOnsightGrade(grade: FrenchScaleGrade) {
        profileRepository.updateClimberProfile {
            when (type) {
                ClimbingType.Sport -> copy(sportLevel = sportLevel.copy(onsightGrade = grade))
                ClimbingType.Bouldering -> copy(boulderLevel = boulderLevel.copy(onsightGrade = grade))
            }
        }
    }

    fun setFlashGrade(grade: FrenchScaleGrade) {
        profileRepository.updateClimberProfile {
            when (type) {
                ClimbingType.Sport -> copy(sportLevel = sportLevel.copy(flashGrade = grade))
                ClimbingType.Bouldering -> copy(boulderLevel = boulderLevel.copy(flashGrade = grade))
            }
        }
    }

}