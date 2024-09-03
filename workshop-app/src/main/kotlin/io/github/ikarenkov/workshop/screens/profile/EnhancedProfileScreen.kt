package io.github.ikarenkov.workshop.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.NavModel
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.ReducerAction
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.lazylist.screenItem
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.forward
import io.github.ikarenkov.workshop.domain.ClimbingType
import io.github.ikarenkov.workshop.screens.TrainingRecommendationsScreen
import io.github.ikarenkov.workshop.screens.climbing_level.ClimbingLevelScreen
import io.github.ikarenkov.workshop.screens.personal_data.ClimberPersonalInfoScreen
import io.github.ikarenkov.workshop.screens.profile_setup.ProfileSetupFlowScreenFinal
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
class EnhancedProfileScreen(
    private val navModel: NavModel<EnhancedProfileNavigationState, EnhancedProfileNavigationAction> = NavModel(EnhancedProfileNavigationState())
) : ContainerScreen<EnhancedProfileNavigationState, EnhancedProfileNavigationAction>(
    navModel
) {

    @Composable
    override fun Content(modifier: Modifier) {
        val navigation = LocalStackNavigation.current
        val viewModel = koinViewModel<EnhancedProfileViewModel>() {
            parametersOf(this)
        }
        val state by viewModel.state.collectAsState()
        EnhancedProfileContent(
            name = state.name.orEmpty(),
            description = state.description.orEmpty(),
            finishedClimbingSetup = state.finishedClimbingSetup,
            onSetupProfileClick = { restart ->
                navigation.forward(ProfileSetupFlowScreenFinal(restart))
            },
            onViewInsightsClick = {
                navigation.forward(TrainingRecommendationsScreen())
            },
            modifier = modifier
        ) {
            navigationState.climbingProfileScreen?.let { screen ->
                screenItem(screen) {
                    Card {
                        InternalContent(screen)
                    }
                }
            }
            navigationState.sportLevelScreen?.let { screen ->
                screenItem(screen) {
                    Card {
                        InternalContent(screen)
                    }
                }
            }
            navigationState.boulderingLevelScreen?.let { screen ->
                screenItem(screen) {
                    Card {
                        InternalContent(screen)
                    }
                }
            }
        }
    }
}

@Composable
private fun EnhancedProfileContent(
    name: String,
    description: String,
    finishedClimbingSetup: Boolean,
    onSetupProfileClick: (restart: Boolean) -> Unit,
    onViewInsightsClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit = {}
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            ProfileHeader(name, description)
        }
        item {
            Card {
                Column(
                    Modifier.padding(16.dp)
                ) {
                    if (finishedClimbingSetup) {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = onViewInsightsClick
                        ) {
                            Text("View insights")
                        }
                    }
                    if (finishedClimbingSetup) {
                        OutlinedButton(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                onSetupProfileClick(true)
                            },
                        ) {
                            Text("Restart insights setup")
                        }
                    } else {
                        Button(
                            modifier = Modifier
                                .fillMaxWidth(),
                            onClick = {
                                onSetupProfileClick(false)
                            },
                        ) {
                            Text("Get climbing insights")
                        }
                    }
                }
            }
        }
        content()
    }
}

@Parcelize
data class EnhancedProfileNavigationState(
    val climbingProfileScreen: ClimberPersonalInfoScreen? = null,
    val sportLevelScreen: ClimbingLevelScreen? = null,
    val boulderingLevelScreen: ClimbingLevelScreen? = null,
) : NavigationState {

    override fun getChildScreens(): List<Screen> = listOfNotNull(
        climbingProfileScreen,
        sportLevelScreen,
        boulderingLevelScreen
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

@Preview
@Composable
private fun PreviewEnhancedProfile() {
    EnhancedProfileContent(
        name = "Igor Karenkov",
        description = "Software Engineer",
        modifier = Modifier.fillMaxSize(),
        finishedClimbingSetup = true,
        onSetupProfileClick = {},
        onViewInsightsClick = {}
    )
}