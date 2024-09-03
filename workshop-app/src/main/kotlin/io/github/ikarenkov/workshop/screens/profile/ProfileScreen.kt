package io.github.ikarenkov.workshop.screens.profile

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import kotlinx.parcelize.Parcelize

@Parcelize
class ProfileScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        ProfileScreenContent(
            name = "Igor Karenkov",
            description = "Software Engineer",
            modifier = modifier,
            onSetupProfileClick = {
                // TODO: Workshop 4.2 - navigate to ProfileSetupFlowScreen
            }
        )
    }
}