package com.github.terrakok.androidcomposeapp.screens.dialogs

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.LifecycleEventObserver
import com.github.terrakok.androidcomposeapp.screens.SampleScreenContent
import com.github.terrakok.androidcomposeapp.screens.base.SampleButtonsContent
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.LocalContainerScreen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.lifecycle.LifecycleScreenEffect
import com.github.terrakok.modo.stack.StackScreen
import com.github.terrakok.modo.stack.back
import kotlinx.parcelize.Parcelize
import logcat.logcat

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

    @Composable
    override fun Content() {
        LifecycleScreenEffect {
            LifecycleEventObserver { _, event ->
                logcat(tag = "SampleDialog") { "$screenKey $event" }
            }
        }
        val container = LocalContainerScreen.current as StackScreen
        if (systemDialog) {
            Box(
                Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
            ) {
                if (dialogsPlayground) {
                    DialogsPlaygroundContent(screenIndex)
                } else {
                    SampleScreenContent(screenIndex, container, isDialog = true)
                }
            }
        } else {
            val state = remember {
                MutableTransitionState(false).apply {
                    // Start the animation immediately.
                    targetState = true
                }
            }
            LaunchedEffect(key1 = state.currentState) {
                if (state.isIdle && !state.currentState) {
                    logcat { "container.back()" }
                    container.back()
                }
            }
            BackHandler {
                state.targetState = false
            }
            AnimatedVisibility(visibleState = state, enter = fadeIn(), exit = fadeOut()) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(color = Color.DarkGray.copy(alpha = 0.7f))
                            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                                state.targetState = false
                            }
                    )
                    SampleButtonsContent(
                        screenIndex = screenIndex,
                        buttonsState = rememberDialogsButtons(LocalContainerScreen.current as StackScreen, screenIndex),
                        isDialog = true,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(horizontal = 50.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .clickable(
                                indication = null,
                                interactionSource = remember {
                                    MutableInteractionSource()
                                }
                            ) {}
                    )
                }
            }
        }
    }
}