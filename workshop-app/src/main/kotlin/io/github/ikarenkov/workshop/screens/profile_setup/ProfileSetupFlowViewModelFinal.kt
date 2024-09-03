package io.github.ikarenkov.workshop.screens.profile_setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.terrakok.modo.navigationStateFlow
import com.github.terrakok.modo.stack.StackNavContainer
import com.github.terrakok.modo.stack.StackState
import com.github.terrakok.modo.stack.back
import com.github.terrakok.modo.stack.forward
import com.github.terrakok.modo.stack.setState
import io.github.ikarenkov.workshop.data.ClimberProfileRepository
import io.github.ikarenkov.workshop.domain.ClimberProfile
import io.github.ikarenkov.workshop.domain.ClimbingType
import io.github.ikarenkov.workshop.screens.TrainingRecommendationsScreen
import io.github.ikarenkov.workshop.screens.climbing_level.ClimbingLevelScreen
import io.github.ikarenkov.workshop.screens.personal_data.ClimberPersonalInfoScreen
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

// Workshop 5.1 - create VM
class ProfileSetupFlowViewModelFinal(
    private val restartFlow: Boolean,
    // Workshop 5.1.1 - take screens as parametrs
    private val profileSetupScreen: ProfileSetupFlowScreenFinal,
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
        profileSetupScreen.setState(StackState(getInitialScreensList(startStep)))
    }

    val state: StateFlow<ProfileSetupContainerUiState> = combine(
        profileSetupScreen.navigationStateFlow(),
        climberProfileRepository.climberProfile
    ) { navigationState, profile ->
        getUiState(navigationState, profile)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        getUiState(profileSetupScreen.navigationState, climberProfileRepository.climberProfile.value)
    )

    @Suppress("MagicNumber")
    fun getInitialScreensList(step: Int) = listOfNotNull(
        ClimberPersonalInfoScreen(),
        if (step >= 2) ClimbingLevelScreen(ClimbingType.Sport) else null,
        if (step >= 3) ClimbingLevelScreen(ClimbingType.Bouldering) else null,
        if (step >= 4) TrainingRecommendationsScreen() else null
    )

    private fun getUiState(
        navigationState: StackState,
        profile: ClimberProfile
    ) = ProfileSetupContainerUiState(
        continueEnabled = isContinueEnabled(navigationState.stack.size, profile),
        currentStep = navigationState.stack.size,
        stepsCount = 4,
        title = navigationState.stack.lastOrNull()?.let { it as? SetupStepScreen }?.title ?: "Profile Setup"
    )

    // Workshop 5.2.1 - move onContinueClick from ProfileSetupFlowScreen
    fun onContinueClick() {
        when (val screen = profileSetupScreen.navigationState.stack.lastOrNull()) {
            is ClimberPersonalInfoScreen -> {
                profileSetupScreen.forward(ClimbingLevelScreen(ClimbingType.Sport))
            }
            is ClimbingLevelScreen -> {
                if (screen.climbingType == ClimbingType.Bouldering) {
                    profileSetupScreen.forward(TrainingRecommendationsScreen())
                } else {
                    profileSetupScreen.forward(ClimbingLevelScreen(ClimbingType.Bouldering))
                }
            }
            is TrainingRecommendationsScreen -> {
                parentNavigation.back()
            }
        }
    }

    // Workshop 5.2.2 - move onCancelClick from ProfileSetupFlowScreen
    fun onCancelClick() {
        parentNavigation.back()
    }

    // Workshop 5.2.3 - move onBackClick from ProfileSetupFlowScreen
    fun onBackClick() {
        if (profileSetupScreen.navigationState.stack.size > 1) {
            profileSetupScreen.back()
        } else {
            parentNavigation.back()
        }
    }

}