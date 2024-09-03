package io.github.ikarenkov.workshop.screens.profile

import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.NavModel
import com.github.terrakok.modo.lazylist.screenItem
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.forward
import io.github.ikarenkov.workshop.screens.TrainingRecommendationsScreen
import io.github.ikarenkov.workshop.screens.profile_setup.ProfileSetupFlowScreenFinal
import kotlinx.parcelize.Parcelize
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Parcelize
class EnhancedProfileScreenFinal(
    private val navModel: NavModel<EnhancedProfileNavigationState, EnhancedProfileNavigationAction> = NavModel(EnhancedProfileNavigationState())
) : ContainerScreen<EnhancedProfileNavigationState, EnhancedProfileNavigationAction>(
    navModel
) {

    @Composable
    override fun Content(modifier: Modifier) {
        val navigation = LocalStackNavigation.current
        val viewModel = koinViewModel<EnhancedProfileViewModelFinal> {
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