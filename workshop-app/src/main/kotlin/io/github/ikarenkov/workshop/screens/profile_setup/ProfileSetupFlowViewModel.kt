package io.github.ikarenkov.workshop.screens.profile_setup

import androidx.lifecycle.ViewModel
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.stack.StackState
import io.github.ikarenkov.workshop.data.ClimberProfileRepository
import io.github.ikarenkov.workshop.domain.ClimberProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// TODO: Workshop 5.1 - use VM
class ProfileSetupFlowViewModel(
    // TODO: Workshop 5.1.3 - pass ProfileSetupFlowScreen and parent stack navigation as parameters
//    private val profileSetupFlowScreen: ProfileSetupFlowScreen,
//    private val parentNavigation: StackNavContainer,
    @Suppress("UnusedPrivateProperty")
    private val climberProfileRepository: ClimberProfileRepository,
) : ViewModel() {

    // TODO: Workshop 5.4 - set navigation initial state using getProfileSetupStartingStep and getProfileSetupInitialScreens

    // TODO: Workshop 5.3.1 - define state using navigationStateFlow and climberProfileRepository.climberProfile
    //  Use combineStateFlow, navigationStateStateFlow, climberProfileRepository.climberProfile and getUiState
//    val state: StateFlow<ProfileSetupContainerUiState> = combineStateFlow(
//        profileSetupFlowScreen.navigationStateStateFlow(viewModelScope),
//        climberProfileRepository.climberProfile,
//        viewModelScope
//    ) { navigationState, profile ->
//        getUiState(navigationState, profile)
//    }

    fun onContinueClick() {
        // TODO: Workshop 5.2.1 - move onContinueClick from ProfileSetupFlowScreen
//        getNextProfileSetupStepScreen(profileSetupFlowScreen.navigationState.stack.size)
//            ?.let { profileSetupFlowScreen.forward(it) }
//            ?: parentNavigation.back()
    }

    fun onCancelClick() {
        // TODO: Workshop 5.2.2 - move onCancelClick from ProfileSetupFlowScreen
//        parentNavigation.back()
    }

    fun onBackClick() {
        // TODO: Workshop 5.2.3 - move onBackClick from ProfileSetupFlowScreen
//        if (profileSetupFlowScreen.navigationState.stack.size > 1) {
//            profileSetupFlowScreen.back()
//        } else {
//            parentNavigation.back()
//        }
    }

}

fun getUiState(
    navigationState: StackState,
    profile: ClimberProfile
) = ProfileSetupContainerUiState(
    continueEnabled = isContinueEnabled(navigationState.stack.size, profile),
    currentStep = navigationState.stack.size,
    stepsCount = 4,
    title = navigationState.stack.lastOrNull()?.let { it as? SetupStepScreen }?.title ?: "Profile Setup"
)

@Suppress("MagicNumber")
fun isContinueEnabled(
    currentStep: Int,
    profile: ClimberProfile
) = when (currentStep) {
    1 -> profile.dateOfBirth != null && profile.heightSm != null && profile.weightKg != null
    2 -> profile.sportLevel.hasAllGrades()
    3 -> profile.boulderLevel.hasAllGrades()
    else -> true
}