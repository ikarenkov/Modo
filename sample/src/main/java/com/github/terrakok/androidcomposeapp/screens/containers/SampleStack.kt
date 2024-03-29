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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.github.terrakok.androidcomposeapp.SlideTransition
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.stack.DialogPlaceHolder
import com.github.terrakok.modo.stack.StackNavModel
import com.github.terrakok.modo.stack.StackScreen
import com.github.terrakok.modo.stack.back
import kotlinx.parcelize.Parcelize

@Parcelize
open class SampleStack(
    private val stackNavModel: StackNavModel
) : StackScreen(stackNavModel) {

    constructor(rootScreen: Screen) : this(StackNavModel(rootScreen))

    @Composable
    override fun Content(modifier: Modifier) {
        TopScreenContent(
            Modifier
                .windowInsetsPadding(WindowInsets.systemBars)
                .fillMaxSize()
        ) { modifier ->
            SlideTransition(modifier)
        }
    }

    @OptIn(ExperimentalModoApi::class, ExperimentalComposeUiApi::class)
    @Composable
    override fun DecorateCustomDialog(dialog: DialogScreen, modifier: Modifier, content: @Composable (Modifier) -> Unit) {
        val isDialogPlaceHolder = dialog is DialogPlaceHolder
        val background by animateColorAsState(
            targetValue = if (isDialogPlaceHolder) Color.Transparent else Color.Black.copy(alpha = 0.5f),
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