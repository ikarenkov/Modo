package io.github.ikarenkov.workshop.screens.profile_setup

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.animation.SlideTransition
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.StackNavModel
import com.github.terrakok.modo.stack.StackScreen
import io.github.ikarenkov.workshop.screens.personal_data.ClimberPersonalInfoScreenFinal
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

// Workshop 4.1 - custom container
@OptIn(ExperimentalModoApi::class)
@Parcelize
class ProfileSetupFlowScreenFinal(
    private val restartFlow: Boolean = false,
    // Workshop 4.1.1 - stack nav model with initial screen
    private val navModel: StackNavModel = StackNavModel(ClimberPersonalInfoScreenFinal())
) : StackScreen(navModel) {

    // FIXME
//    @ExperimentalModoApi
//    override fun provideDialogPlaceholderScreen(): DialogScreen? = null

    @Suppress("MagicNumber")
    @Composable
    override fun Content(modifier: Modifier) {
        val parentNavigation = LocalStackNavigation.current
        // Workshop 5.1.3 - pass parameters
        val viewModel = koinViewModel<ProfileSetupFlowViewModelFinal> { parametersOf(restartFlow, this, parentNavigation) }
        val state by viewModel.state.collectAsState()
        ProfileSetupFlowContainerContent(
            modifier = modifier,
            state = state,
            onContinueClick = {
                // Workshop 4.2.1 - navigation based on selected screen
                // Workshop 5.2.1 - move to VM
                viewModel.onContinueClick()
            },
            onCancelClick = {
                // Workshop 4.2.2 - navigate back
                // Workshop 5.2.3 - move to VM
                viewModel.onCancelClick()
            },
            onBackClick = {
                // Workshop 4.2.3 - pass navigation to parent
                // Workshop 5.2.3 - move to VM
                viewModel.onBackClick()
            }
        ) { contentModifier ->
            // Workshop 4.1.2 - display content
            TopScreenContent(contentModifier) { transitionModifier ->
                // Workshop 4.3 - custom animation
                SlideTransition(transitionModifier)
            }
        }

    }

}