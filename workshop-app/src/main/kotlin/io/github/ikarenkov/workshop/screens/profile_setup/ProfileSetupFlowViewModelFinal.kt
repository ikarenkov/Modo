package io.github.ikarenkov.workshop.screens.profile_setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.modo.navigationStateStateFlow
import com.github.terrakok.modo.stack.StackNavContainer
import com.github.terrakok.modo.stack.StackState
import com.github.terrakok.modo.stack.back
import com.github.terrakok.modo.stack.forward
import com.github.terrakok.modo.stack.setState
import io.github.ikarenkov.workshop.core.combineStateFlow
import io.github.ikarenkov.workshop.data.ClimberProfileRepository
import io.github.ikarenkov.workshop.domain.ClimbingType
import io.github.ikarenkov.workshop.screens.TrainingRecommendationsScreen
import io.github.ikarenkov.workshop.screens.climbing_level.ClimbingLevelScreen
import io.github.ikarenkov.workshop.screens.personal_data.ClimberPersonalInfoScreenFinal
import kotlinx.coroutines.flow.StateFlow

// Workshop 5.1 - create VM
class ProfileSetupFlowViewModelFinal(
    private val restartFlow: Boolean,
    // Workshop 5.1.1 - take screens as parametrs
    private val profileSetupFlowScreen: ProfileSetupFlowScreenFinal,
    private val parentNavigation: StackNavContainer,
    private val climberProfileRepository: ClimberProfileRepository,
) : ViewModel() {

    init {
        val startStep =
            @Suppress("MagicNumber")
            if (restartFlow) {
                1
            } else {
                climberProfileRepository.climberProfile.value.let { profile ->
                    when {
                        profile.boulderLevel.hasAllGrades() -> 4
                        profile.sportLevel.hasAllGrades() -> 3
                        profile.dateOfBirth != null && profile.heightSm != null && profile.weightKg != null -> 2
                        else -> 1
                    }
                }
            }
        profileSetupFlowScreen.setState(StackState(getInitialScreensList(startStep)))
    }

    // Workshop 5.3 - define state using navigationStateFlow and climberProfileRepository.climberProfile
    val state: StateFlow<ProfileSetupContainerUiState> = combineStateFlow(
        profileSetupFlowScreen.navigationStateStateFlow(viewModelScope),
        climberProfileRepository.climberProfile,
        viewModelScope,
    ) { navigationState, profile ->
        getUiState(navigationState, profile)
    }

    @Suppress("MagicNumber")
    fun getInitialScreensList(step: Int) = listOfNotNull(
        ClimberPersonalInfoScreenFinal(),
        if (step >= 2) ClimbingLevelScreen(ClimbingType.Sport) else null,
        if (step >= 3) ClimbingLevelScreen(ClimbingType.Bouldering) else null,
        if (step >= 4) TrainingRecommendationsScreen() else null
    )

    // Workshop 5.2.1 - move onContinueClick from ProfileSetupFlowScreen
    fun onContinueClick() {
        when (profileSetupFlowScreen.navigationState.stack.size) {
            1 -> profileSetupFlowScreen.forward(ClimbingLevelScreen(ClimbingType.Sport))
            2 -> profileSetupFlowScreen.forward(ClimbingLevelScreen(ClimbingType.Bouldering))
            3 -> profileSetupFlowScreen.forward(TrainingRecommendationsScreen())
            else -> parentNavigation.back()
        }
    }

    // Workshop 5.2.2 - move onCancelClick from ProfileSetupFlowScreen
    fun onCancelClick() {
        parentNavigation.back()
    }

    // Workshop 5.2.3 - move onBackClick from ProfileSetupFlowScreen
    fun onBackClick() {
        if (profileSetupFlowScreen.navigationState.stack.size > 1) {
            profileSetupFlowScreen.back()
        } else {
            parentNavigation.back()
        }
    }

}