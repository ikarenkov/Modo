package com.github.terrakok.modo.containers

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Dialog
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.RendererContent
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.defaultRendererContent

abstract class StackScreen(
    navigationModel: NavigationModel<StackNavigationState>
) : ContainerScreen<StackNavigationState>(navigationModel), Parcelable {

    override val reducer: NavigationReducer<StackNavigationState> = StackReducer()

    /**
     * Default implementation last screen from stack.
     */
    @Composable
    override fun Content() {
        TopScreenContent()
    }

    /**
     * Renders last screen from stack.
     */
    @Composable
    protected fun TopScreenContent(
        content: RendererContent = defaultRendererContent
    ) {
        val screensToRender: List<Screen> by remember {
            derivedStateOf {
                val stack = navigationState.stack
                val topScreen = stack.last()
                if (topScreen is DialogScreen) {
                    val screen = stack.findLast { it !is DialogScreen }
                    listOfNotNull(screen, topScreen)
                } else {
                    listOf(stack.last())
                }
            }
        }
        for (screen in screensToRender) {
            if (screen is DialogScreen) {
                Dialog(
                    onDismissRequest = { back() },
                    properties = remember { screen.provideDialogProperties() }
                ) {
                    Content(screen = screen, content)
                }
            } else {
                Content(screen, content)
            }
        }
    }

    @Composable
    protected fun Content(
        screen: Screen,
        content: RendererContent = defaultRendererContent
    ) {
        super.InternalContent(screen, content)
    }

}