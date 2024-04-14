package com.github.terrakok.modo.stack

import android.os.Parcelable
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat
import com.github.terrakok.modo.ComposeRenderer
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.RendererContent
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.defaultRendererContent
import com.github.terrakok.modo.generateScreenKey
import kotlinx.parcelize.Parcelize

abstract class StackScreen(
    navigationModel: StackNavModel
) : ContainerScreen<StackState>(navigationModel), Parcelable {

    override val reducer: NavigationReducer<StackState> = StackReducer()
    open val defaultBackHandler: Boolean = true

    /**
     * Default implementation last screen from stack.
     */
    @Composable
    override fun Content(modifier: Modifier) {
        TopScreenContent(modifier)
    }

    /**
     * The palace holder screen that is used to support animation of showing first dialog appearance.
     * You can return null to handle appearance animation by yourself.
     * For more details check out [DialogPlaceHolder] and [TopScreenContent].
     */
    @OptIn(ExperimentalModoApi::class)
    open fun provideDialogPlaceholderScreen(): DialogScreen? = DialogPlaceHolder()

    /**
     * Renders last screen from stack.
     */
    @OptIn(ExperimentalModoApi::class)
    @Composable
    protected fun TopScreenContent(
        modifier: Modifier = Modifier,
        dialogModifier: Modifier = Modifier,
        content: RendererContent<StackState> = defaultRendererContent
    ) {
        val screensToRender: ScreensToRender by rememberScreensToRender()
        screensToRender.screen?.let { screen ->
            Content(screen, modifier, content)
        }
        val dialogPlaceHolder = rememberSaveable {
            OptionalScreen(provideDialogPlaceholderScreen())
        }.screen
        val dialogs = remember {
            derivedStateOf {
                screensToRender.dialogs.ifEmpty {
                    listOfNotNull(dialogPlaceHolder)
                }
            }
        }
        for (dialog in dialogs.value) {
            RenderDialog(dialog, content, dialogModifier)
        }
    }

    @OptIn(ExperimentalModoApi::class)
    @Composable
    protected open fun DecorateCustomDialog(
        dialog: DialogScreen,
        modifier: Modifier,
        content: @Composable (Modifier) -> Unit
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            content(modifier)
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
    private fun StackScreen.RenderDialog(dialog: DialogScreen, content: RendererContent<StackState>, modifier: Modifier) {
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
                    DisposableEffect(parent) {
                        (parent as? DialogWindowProvider)?.window?.let { window ->
                            WindowCompat.setDecorFitsSystemWindows(window, false)
                            if (!dialogConfig.useSystemDim) {
                                window.setDimAmount(0f)
                            }
                        }
                        onDispose { }
                    }
                    Content(dialog, modifier, content)
                }
            }
            DialogScreen.DialogConfig.Custom -> {
                DecorateCustomDialog(dialog, modifier) {
                    Content(dialog, modifier, content)
                }
            }
        }
    }

    @Composable
    protected fun Content(
        screen: Screen,
        modifier: Modifier = Modifier,
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
        super.InternalContent(screen, modifier, content)
    }

    @OptIn(ExperimentalModoApi::class)
    @Stable
    data class ScreensToRender(
        val screen: Screen?,
        val dialogs: List<DialogScreen>,
    )

}

@Stable
@Parcelize
private data class OptionalScreen<T : Screen?>(
    val screen: T
) : Parcelable

/**
 * Special dialog that is used to support Screen.Transition in dialogs.
 * It adds as a default transparent dialogs if there are no other dialogs.
 * So this helps to animate content. Without it there is no animation for the first dialog.
 */
@Parcelize
@OptIn(ExperimentalModoApi::class)
data class DialogPlaceHolder(
    override val screenKey: ScreenKey = generateScreenKey()
) : DialogScreen {

    override fun provideDialogConfig() = DialogScreen.DialogConfig.Custom

    @Composable
    override fun Content(modifier: Modifier) {
        Box(
            // ignore modifier, because it is just invisible placeholder
            Modifier.fillMaxSize()
        )
    }

}