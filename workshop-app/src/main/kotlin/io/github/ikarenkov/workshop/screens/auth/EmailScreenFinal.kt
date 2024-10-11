package io.github.ikarenkov.workshop.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.StackNavContainer
import com.github.terrakok.modo.stack.forward
import kotlinx.parcelize.Parcelize

@Parcelize
class EmailScreenFinal(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) {
        val navigation: StackNavContainer = LocalStackNavigation.current
        EmailScreenContent(
            modifier = modifier,
            onContinueClick = { email ->
                navigation.forward(AuthCodeScreen())
            }
        )
    }
}