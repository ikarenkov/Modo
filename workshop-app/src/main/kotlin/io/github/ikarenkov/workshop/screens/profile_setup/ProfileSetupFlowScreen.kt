package io.github.ikarenkov.workshop.screens.profile_setup

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import kotlinx.parcelize.Parcelize

@Parcelize
class ProfileSetupFlowScreen(
// TODO: Workshop 4.1 - implement navModel in constructor and pass it to StackScreen, use ClimberPersonalInfoScreen as initial screen
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        // TODO: Workshop 4.4 - use navigation state to retrieve current step and title
        val state = ProfileSetupContainerUiState(
            title = "Step #1",
            currentStep = 1,
            stepsCount = 4,
            continueEnabled = true
        )
        ProfileSetupFlowContainerContent(
            modifier = modifier,
            state = state,
            onContinueClick = {
                // TODO: Workshop 4.3.1 - navigation based on selected screen
                // TODO: 5.2.1 - move to VM
            },
            onCancelClick = {
                // TODO: Workshop 4.3.2 - navigate back
                // TODO: 5.2.2 - move to VM
            },
            onBackClick = {
                // TODO: Workshop 4.3.3 - pass navigation to parent
                // TODO: 5.2.3 - move to VM
            },
        ) { modifier ->
            // TODO: Workshop 4.1.3 - display content
            // TODO: Workshop 4.5 - custom animation
        }
    }

}