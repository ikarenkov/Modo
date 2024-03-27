package com.github.terrakok.modo.stack

import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import com.github.terrakok.modo.ComposeRenderer
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
        val screensToRender: ScreensToRender by rememberScreensToRender()
        screensToRender.screen?.let { screen ->
            Content(screen, content)
        }
        if (screensToRender.dialogs.isNotEmpty()) {
            for (dialog in screensToRender.dialogs) {
                RenderDialog(dialog, content)
            }
        }
    }

    @Composable
    @OptIn(ExperimentalModoApi::class)
    private fun rememberScreensToRender() = remember {
        derivedStateOf {
            val stack = navigationState.stack
            val topScreen = stack.lastOrNull()
            if (topScreen is DialogScreen) {
                val screen = stack.findLast { it !is DialogScreen }!!
                val dialogs = mutableListOf<DialogScreen>()
                for (dialog in stack.reversed()) {
                    if (dialog !is DialogScreen) {
                        break
                    }
                    val needRender = dialog.permanentDialog || dialogs.isEmpty()
                    if (needRender) {
                        dialogs += dialog
                    }
                }
                ScreensToRender(screen, dialogs.reversed())
            } else {
                ScreensToRender(topScreen, emptyList())
            }
        }
    }

    @Composable
    @OptIn(ExperimentalModoApi::class)
    private fun StackScreen.RenderDialog(dialog: DialogScreen, content: RendererContent<StackState>) {
        val dialogConfig = remember(dialog) {
            dialog.provideDialogConfig()
        }
        when (dialogConfig) {
            is DialogScreen.DialogConfig.System -> {
                Dialog(
                    onDismissRequest = { back() },
                    properties = dialogConfig.dialogProperties
                ) {
                    DisposableEffect(Unit) {
                        onDispose {
                            (renderer as ComposeRenderer).transitionCompleteChannel.trySend(Unit)
                        }
                    }
                    val parent = LocalView.current.parent
                    LaunchedEffect(key1 = parent) {
                        if (!dialogConfig.useSystemDim) {
                            (parent as? DialogWindowProvider)?.window?.setDimAmount(0f)
                        }
                    }
                    Content(dialog, content)
                }
            }
            DialogScreen.DialogConfig.Custom -> {
                Content(dialog, content)
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

    @OptIn(ExperimentalModoApi::class)
    @Stable
    data class ScreensToRender(
        val screen: Screen?,
        val dialogs: List<DialogScreen>,
    )

}