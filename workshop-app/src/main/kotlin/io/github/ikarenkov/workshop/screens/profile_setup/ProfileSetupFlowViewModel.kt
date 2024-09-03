package io.github.ikarenkov.workshop.screens.profile_setup

import androidx.lifecycle.ViewModel
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.stack.StackState
import io.github.ikarenkov.workshop.data.ClimberProfileRepository
import io.github.ikarenkov.workshop.domain.ClimberProfile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// TODO: Workshop 5.1 - create VM
class ProfileSetupFlowViewModel(
    // TODO: Workshop 5.1.1 - pass ProfileSetupFlowScreen as parameter
    // TODO: Workshop 5.1.2 - pass parent stack navigation as parameter
    @Suppress("UnusedPrivateProperty")
    private val climberProfileRepository: ClimberProfileRepository,
) : ViewModel() {

    // TODO: Workshop 5.4.2 - get starting step based on filled data and set a state to the navigation using getInitialScreensList

    @Suppress("MagicNumber", "UnusedPrivateMember")
    private fun getStartingStep(profile: ClimberProfile) = when {
        profile.boulderLevel.hasAllGrades() -> 4
        profile.sportLevel.hasAllGrades() -> 3
        profile.dateOfBirth != null && profile.heightSm != null && profile.weightKg != null -> 2
        else -> 1
    }

    // TODO: Workshop 5.3 - define state using navigationStateFlow and climberProfileRepository.climberProfile
    val state: StateFlow<ProfileSetupContainerUiState> = MutableStateFlow(
        ProfileSetupContainerUiState(
            continueEnabled = true,
            currentStep = 1,
            stepsCount = 4,
            title = "Step #1"
        )
    )

    @Suppress("UnusedParameter")
    // TODO: Workshop 5.4.1 - implement getInitialScreensList
    fun getInitialScreensList(step: Int): List<Screen> = listOfNotNull()

    fun onContinueClick() {
        // TODO: Workshop 5.2.1 - move onContinueClick from ProfileSetupFlowScreen
    }

    fun onCancelClick() {
        // TODO: Workshop 5.2.2 - move onCancelClick from ProfileSetupFlowScreen
    }

    fun onBackClick() {
        // TODO: Workshop 5.2.3 - move onBackClick from ProfileSetupFlowScreen
    }

}

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

fun getUiState(
    navigationState: StackState,
    currentStep: Int,
    profile: ClimberProfile
) = ProfileSetupContainerUiState(
    continueEnabled = isContinueEnabled(currentStep, profile),
    currentStep = currentStep,
    stepsCount = 4,
    title = navigationState.stack.lastOrNull()?.let { it as? SetupStepScreen }?.title ?: "Profile Setup"
)