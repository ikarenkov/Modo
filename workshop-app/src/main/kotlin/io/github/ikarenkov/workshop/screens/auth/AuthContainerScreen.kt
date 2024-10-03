package io.github.ikarenkov.workshop.screens.auth

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.animation.ScreenTransition
import com.github.terrakok.modo.stack.StackNavModel
import com.github.terrakok.modo.stack.StackScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class AuthContainerScreen(
    private val navModel: StackNavModel = StackNavModel(EmailScreen())
) : StackScreen(navModel) {

    @Composable
    override fun Content(modifier: Modifier) {
        TopScreenContent(modifier) { modifier ->
            ScreenTransition(modifier)
        }
    }
}