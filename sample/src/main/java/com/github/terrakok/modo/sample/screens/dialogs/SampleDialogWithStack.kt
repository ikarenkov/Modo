package com.github.terrakok.modo.sample.screens.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.LifecycleEventObserver
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.lifecycle.LifecycleScreenEffect
import com.github.terrakok.modo.sample.SlideTransition
import com.github.terrakok.modo.sample.screens.MainScreen
import com.github.terrakok.modo.stack.StackNavModel
import com.github.terrakok.modo.stack.StackScreen
import kotlinx.parcelize.Parcelize
import logcat.logcat

/**
 * The sample of Dialog with nested navigation.
 */
@OptIn(ExperimentalModoApi::class)
@Parcelize
class SampleDialogWithStack(
    private val i: Int,
    private val systemDialog: Boolean = true,
    override val permanentDialog: Boolean = false,
    private val navModel: StackNavModel = StackNavModel(MainScreen(i + 1))
) : StackScreen(navModel), DialogScreen {

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

    @Suppress("MagicNumber")
    @Composable
    override fun Content(modifier: Modifier) {
        LifecycleScreenEffect {
            LifecycleEventObserver { _, event ->
                logcat(tag = "SampleDialog") { "$screenKey $event" }
            }
        }
        if (systemDialog) {
            Box(
                modifier
                    .fillMaxHeight(0.6f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
            ) {
                TopScreenContent { screenModifier ->
                    SlideTransition(
                        Modifier,
                        screenModifier = screenModifier
                            .fillMaxSize()
                            .background(Color.Black)
                    )
                }
            }
        } else {
            Box(modifier = modifier.fillMaxSize()) {
                TopScreenContent { transitionModifier ->
                    SlideTransition(
                        modifier = transitionModifier
                            .align(Alignment.Center)
                            .padding(horizontal = 50.dp)
                            .clip(RoundedCornerShape(16.dp)),
                        screenModifier = Modifier
                            .background(Color.White)
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
}