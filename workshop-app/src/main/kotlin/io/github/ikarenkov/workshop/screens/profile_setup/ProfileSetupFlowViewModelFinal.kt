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
import kotlinx.coroutines.flow.StateFlow

// Workshop 5.1 - create VM
@Suppress("MagicNumber")
class ProfileSetupFlowViewModelFinal(
    private val restartFlow: Boolean,
    // Workshop 5.1.1 - take screens as parametrs
    private val profileSetupFlowScreen: ProfileSetupFlowScreenFinal,
    private val parentNavigation: StackNavContainer,
    private val climberProfileRepository: ClimberProfileRepository,
) : ViewModel() {

    init {
        val startStep =
            if (restartFlow) 1 else getProfileSetupStartingStep(climberProfileRepository.climberProfile.value)
        profileSetupFlowScreen.setState(StackState(getProfileSetupInitialScreens(startStep)))
    }

    // Workshop 5.3 - define state using navigationStateFlow and climberProfileRepository.climberProfile
    val state: StateFlow<ProfileSetupContainerUiState> = combineStateFlow(
        profileSetupFlowScreen.navigationStateStateFlow(viewModelScope),
        climberProfileRepository.climberProfile,
        viewModelScope,
    ) { navigationState, profile ->
        getUiState(navigationState, profile)
    }

    // Workshop 5.2.1 - move onContinueClick from ProfileSetupFlowScreen
    fun onContinueClick() {
        getNextProfileSetupStepScreen(profileSetupFlowScreen.navigationState.stack.size)?.let {
            profileSetupFlowScreen.forward(it)
        } ?: parentNavigation.back()
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