package com.github.terrakok.modo.stack

import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.window.Dialog
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.RendererContent
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.defaultRendererContent

abstract class StackScreen(
    navigationModel: StackNavModel
) : ContainerScreen<StackState>(navigationModel), Parcelable {

    override val reducer: NavigationReducer<StackState> = StackReducer()
    open val defaultBackHandler: Boolean = true

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
    @OptIn(ExperimentalModoApi::class)
    @Composable
    protected fun TopScreenContent(
        content: RendererContent<StackState> = defaultRendererContent
    ) {
        val screensToRender: Pair<Screen?, DialogScreen?> by remember {
            derivedStateOf {
                val stack = navigationState.stack
                val topScreen = stack.lastOrNull()
                if (topScreen is DialogScreen) {
                    val screen = stack.findLast { it !is DialogScreen }!!
                    screen to topScreen
                } else {
                    topScreen to null
                }
            }
        }
        val (screen, dialog) = screensToRender
        if (screen != null) {
            Content(screen, content)
        }
        if (dialog != null) {
            Dialog(
                onDismissRequest = { back() },
                properties = remember { dialog.provideDialogProperties() }
            ) {
                Content(screen = dialog)
            }
        }
    }

    @Composable
    protected fun Content(
        screen: Screen,
        content: RendererContent<StackState> = defaultRendererContent
    ) {
        val isBackHandlerEnabled by remember {
            derivedStateOf {
                defaultBackHandler && navigationState.getChildScreens().size > 1
            }
        }
        BackHandler(enabled = isBackHandlerEnabled) {
            back()
        }
        super.InternalContent(screen, content)
    }

}