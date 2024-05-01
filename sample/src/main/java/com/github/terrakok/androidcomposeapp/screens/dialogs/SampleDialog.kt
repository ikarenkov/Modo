package com.github.terrakok.androidcomposeapp.screens.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.LifecycleEventObserver
import com.github.terrakok.androidcomposeapp.screens.MainScreenContent
import com.github.terrakok.androidcomposeapp.screens.base.ButtonsScreenContent
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.LocalContainerScreen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.lifecycle.LifecycleScreenEffect
import com.github.terrakok.modo.stack.LocalStackNavigation
import com.github.terrakok.modo.stack.StackScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.parcelize.Parcelize
import logcat.logcat

// Only this solution can arrange correct work.
val showingDialogsCount = MutableStateFlow<Int>(0)

@OptIn(ExperimentalModoApi::class)
@Parcelize
class SampleDialog(
    private val screenIndex: Int,
    private val dialogsPlayground: Boolean,
    private val systemDialog: Boolean = true,
    override val permanentDialog: Boolean = false,
    override val screenKey: ScreenKey = generateScreenKey()
) : DialogScreen {

    override fun provideDialogConfig(): DialogScreen.DialogConfig = if (systemDialog) {
        DialogScreen.DialogConfig.System(
            useSystemDim = true,
            dialogProperties = DialogProperties(
                usePlatformDefaultWidth = true,
                decorFitsSystemWindows = true
            )
        )
    } else {
        DialogScreen.DialogConfig.Custom
    }

    @Suppress("ModifierNotUsedAtRoot")
    @Composable
    override fun Content(modifier: Modifier) {
        SetupSystemBar()
        LifecycleScreenEffect {
            LifecycleEventObserver { _, event ->
                logcat(tag = "SampleDialog") { "$screenKey $event" }
            }
        }
        val navigation = LocalStackNavigation.current
        if (systemDialog) {
            Box(
                modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
            ) {
                if (dialogsPlayground) {
                    DialogsPlaygroundContent(screenIndex, screenKey)
                } else {
                    MainScreenContent(screenIndex, screenKey, navigation)
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                ButtonsScreenContent(
                    screenIndex = screenIndex,
                    screenName = "SampleDialog",
                    screenKey = screenKey,
                    state = rememberDialogsButtons(LocalContainerScreen.current as StackScreen, screenIndex),
                    modifier = modifier
                        .align(Alignment.Center)
                        .padding(horizontal = 50.dp)
                        .clip(RoundedCornerShape(16.dp))
                        // TODO: deal with A11Y and remove it from A11Y tree
                        .clickable(
                            enabled = false,
                            interactionSource = remember {
                                MutableInteractionSource()
                            },
                            indication = null
                        ) {}
                )
            }
        }
    }
}

/**
 * Setups counter of showing dialogs to be able to draw
 */
@OptIn(ExperimentalModoApi::class)
@Composable
internal fun DialogScreen.SetupSystemBar() {
    val needDimSystemBars = remember {
        val dialogConfig = provideDialogConfig()
        dialogConfig is DialogScreen.DialogConfig.System && !dialogConfig.useSystemDim
    }
    if (needDimSystemBars) {
        DisposableEffect(Unit) {
            showingDialogsCount.update { it + 1 }
            onDispose {
                showingDialogsCount.update { it - 1 }
            }
        }
    }
}