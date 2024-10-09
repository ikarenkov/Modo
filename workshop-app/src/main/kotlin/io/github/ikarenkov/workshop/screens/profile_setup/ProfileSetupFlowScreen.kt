package io.github.ikarenkov.workshop.screens.profile_setup

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import io.github.ikarenkov.workshop.screens.TrainingRecommendationsContent
import io.github.ikarenkov.workshop.ui.progress.ProgressBar
import kotlinx.parcelize.Parcelize

@Parcelize
class ProfileSetupFlowScreen(
// TODO: Workshop 4.1 - implement navModel in constructor and pass it to StackScreen, use ClimberPersonalInfoScreen as initial screen
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        // TODO: Workshop 5.1.2 - retrieve viewModel using koinViewModel
        // TODO: Workshop 4.4 - use navigation state to retrieve current step and title
        val state = ProfileSetupContainerUiState(
            // TODO: Workshop 4.4.1 - retrieve title from last screen when it is SetupStepScreen
            title = "Step #1",
            // TODO: Workshop 4.4.2 - set current step based on size of stack
            currentStep = 1,
            stepsCount = 4,
            continueEnabled = true
        )
        ProfileSetupFlowContainerContent(
            modifier = modifier,
            // TODO: Workshop 5.3.2 - use state from viewModel
            state = state,
            onContinueClick = {
                // TODO: Workshop 4.3.1 - navigation based on selected screen. Use `getNextProfileSetupStepScreen`.
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupFlowContainerContent(
    state: ProfileSetupContainerUiState,
    onBackClick: () -> Unit,
    onCancelClick: () -> Unit,
    onContinueClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (Modifier) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        AnimatedContent(
                            targetState = state.title,
                            label = "Title animation",
                            transitionSpec = { fadeIn() togetherWith fadeOut() }
                        ) { title ->
                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = title
                            )
                        }
                        Spacer(Modifier.height(10.dp))
                        ProgressBar(
                            modifier = Modifier.fillMaxWidth(),
                            step = state.currentStep,
                            stepsCount = state.stepsCount
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick
                    ) {
                        Icon(
                            painter = rememberVectorPainter(Icons.AutoMirrored.Filled.ArrowBack),
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onCancelClick
                    ) {
                        Icon(
                            painter = rememberVectorPainter(Icons.Filled.Close),
                            contentDescription = "Exit profile setup"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(Modifier.weight(1f)) {
                content(Modifier.fillMaxHeight())
            }
            Button(
                onClick = onContinueClick,
                enabled = state.continueEnabled,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {
                Text("Continue")
            }
        }
    }
}

data class ProfileSetupContainerUiState(
    val title: String,
    val continueEnabled: Boolean,
    val stepsCount: Int,
    val currentStep: Int
)

@Preview
@Composable
internal fun PreviewProfileSetupContainer() {
    MaterialTheme {
        ProfileSetupFlowContainerContent(
            state = ProfileSetupContainerUiState(
                title = "Step #1",
                currentStep = 1,
                stepsCount = 4,
                continueEnabled = true
            ),
            modifier = Modifier.fillMaxSize(),
            onBackClick = {},
            onCancelClick = {},
            onContinueClick = {},
        ) { modifier ->
            Column(
                modifier
                    .background(Color.Gray)
                    .fillMaxWidth()
            ) {
                Text("Content")
            }
        }
    }
}

@Preview
@Composable
internal fun PreviewProfileSetupFinal() {
    MaterialTheme {
        ProfileSetupFlowContainerContent(
            state = ProfileSetupContainerUiState(
                title = "Step #1",
                currentStep = 1,
                stepsCount = 4,
                continueEnabled = true
            ),
            modifier = Modifier.fillMaxSize(),
            onBackClick = {},
            onCancelClick = {},
            onContinueClick = {},
        ) { modifier ->
            Box {
                TrainingRecommendationsContent(modifier.fillMaxHeight())
            }
        }
    }
}