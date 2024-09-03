package io.github.ikarenkov.workshop.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.modo.ReducerAction
import io.github.ikarenkov.workshop.core.mapStateFlow
import io.github.ikarenkov.workshop.data.ClimberProfileRepository
import io.github.ikarenkov.workshop.domain.ClimberProfile
import io.github.ikarenkov.workshop.domain.ClimbingType
import io.github.ikarenkov.workshop.screens.climbing_level.ClimbingLevelScreen
import io.github.ikarenkov.workshop.screens.personal_data.ClimberPersonalInfoScreen
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class EnhancedProfileViewModelFinal(
    private val enhancedProfileScreenFinal: EnhancedProfileScreenFinal,
    private val climberProfileRepository: ClimberProfileRepository
) : ViewModel() {

    val state: StateFlow<UiState> = climberProfileRepository.climberProfile
        .mapStateFlow(viewModelScope) {
            it.toUiState()
        }

    init {
        viewModelScope.launch {
            climberProfileRepository.climberProfile.collect { profile ->
                enhancedProfileScreenFinal.dispatch(
                    EnhancedProfileNavigationAction(
                        showClimberProfile = profile.dateOfBirth != null,
                        showBoulderLever = profile.boulderLevel.hasAllGrades(),
                        showLeadLevel = profile.sportLevel.hasAllGrades()
                    )
                )
            }
        }
    }

    private fun ClimberProfile.toUiState(): UiState = UiState(
        name = name,
        description = description,
        finishedClimbingSetup = boulderLevel.hasAllGrades() && sportLevel.hasAllGrades()
    )

    data class UiState(
        val name: String?,
        val description: String?,
        val finishedClimbingSetup: Boolean
    )
}

class EnhancedProfileNavigationAction(
    private val showClimberProfile: Boolean,
    private val showLeadLevel: Boolean,
    private val showBoulderLever: Boolean,
) : ReducerAction<EnhancedProfileNavigationState> {
    override fun reduce(oldState: EnhancedProfileNavigationState): EnhancedProfileNavigationState = oldState.copy(
        climbingProfileScreen = if (showClimberProfile) {
            oldState.climbingProfileScreen ?: ClimberPersonalInfoScreen()
        } else {
            null
        },
        sportLevelScreen = if (showLeadLevel) {
            oldState.sportLevelScreen ?: ClimbingLevelScreen(ClimbingType.Sport)
        } else {
            null
        },
        boulderingLevelScreen = if (showBoulderLever) {
            oldState.boulderingLevelScreen ?: ClimbingLevelScreen(ClimbingType.Bouldering)
        } else {
            null
        }
    )

}