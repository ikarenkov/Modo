package io.github.ikarenkov.workshop.screens.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.animation.SlideTransition
import com.github.terrakok.modo.multiscreen.MultiScreen
import com.github.terrakok.modo.multiscreen.MultiScreenNavModel
import com.github.terrakok.modo.multiscreen.selectContainer
import io.github.ikarenkov.workshop.screens.SampleScreenFinal
import io.github.ikarenkov.workshop.screens.profile.EnhancedProfileScreen
import io.github.ikarenkov.workshop.screens.profile.EnhancedProfileScreenFinal
import kotlinx.parcelize.Parcelize

// Workshop 3.1 - create main tab screen
@Parcelize
class MainTabScreenFinal(
// Workshop 3.1.1 - define initial state
    private val navModel: MultiScreenNavModel = MultiScreenNavModel(
        SampleScreenFinal(0),
        EnhancedProfileScreenFinal(),
        selected = 0
    )
) : MultiScreen(navModel) {

    @Composable
    override fun Content(modifier: Modifier) {
        MainTabContent(
            modifier = modifier,
            // Workshop 3.4 - use navigation state to define UI
            selectedTabPos = navigationState.selected,
            onTabClick = { pos ->
                // Workshop 3.3 - navigate between tabs
                selectContainer(pos)
            }
        ) { paddingValues ->
            // Workshop 3.2 - display selected screen
            SelectedScreen(
                Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) { modifier ->
                // Workshop 3.5 - support animation using build-in SlideTransition
                SlideTransition(modifier)
            }

        }
    }
}