package com.github.terrakok.androidcomposeapp.screens.containers

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LifecycleEventObserver
import com.github.terrakok.androidcomposeapp.SlideTransition
import com.github.terrakok.androidcomposeapp.screens.dialogs.SampleBottomSheet
import com.github.terrakok.androidcomposeapp.screens.dialogs.SampleBottomSheetStack
import com.github.terrakok.androidcomposeapp.screens.dialogs.showingDialogsCount
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.lifecycle.LaunchedScreenEffect
import com.github.terrakok.modo.lifecycle.LifecycleScreenEffect
import com.github.terrakok.modo.stack.DialogPlaceHolder
import com.github.terrakok.modo.stack.StackNavModel
import com.github.terrakok.modo.stack.StackScreen
import com.github.terrakok.modo.stack.back
import kotlinx.parcelize.Parcelize
import logcat.logcat

@Parcelize
open class SampleStack(
    private val stackNavModel: StackNavModel
) : StackScreen(stackNavModel) {

    constructor(rootScreen: Screen) : this(StackNavModel(rootScreen))

    @OptIn(ExperimentalModoApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        LifecycleScreenEffect {
            LifecycleEventObserver { _, event ->
                logcat(tag = "SampleStack") { "$screenKey lifecycle event $event" }

            }
        }
        TopScreenContent(
            Modifier.fillMaxSize()
        ) { modifier ->
            SlideTransition(modifier)
        }
    }

    @OptIn(ExperimentalModoApi::class)
    @Composable
    override fun DecorateCustomDialog(dialog: DialogScreen, modifier: Modifier, content: @Composable (Modifier) -> Unit) {
        val isDialogPlaceHolder = remember(dialog) {
            dialog is DialogPlaceHolder
        }
        val isBottomSheet = remember {
            dialog is SampleBottomSheet || dialog is SampleBottomSheetStack
        }
        val background by animateColorAsState(
            targetValue = if (isDialogPlaceHolder || isBottomSheet) Color.Transparent else Color.Black.copy(alpha = 0.5f),
            label = "Dialog dim"
        )
        val backgroundClickableModifier = if (!isDialogPlaceHolder) {
            Modifier
                .clickable(
                    interactionSource = remember {
                        MutableInteractionSource()
                    },
                    indication = null
                ) {
                    back()
                }
        } else {
            Modifier
        }
        Box(
            modifier = modifier
                .fillMaxSize()
                .then(backgroundClickableModifier)
                .background(background),
        ) {
            content(Modifier)
        }
    }
}