package com.github.terrakok.modo.sample

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.ComposeRendererScope
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.animation.ScreenTransition
import com.github.terrakok.modo.animation.StackTransitionType
import com.github.terrakok.modo.animation.calculateStackTransitionType
import com.github.terrakok.modo.stack.StackState

@Composable
@OptIn(ExperimentalModoApi::class)
fun ComposeRendererScope<StackState>.SlideTransition(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier
) {
    ScreenTransition(
        modifier = modifier,
        screenModifier = screenModifier,
        transitionSpec = {
            val transitionType = calculateStackTransitionType(oldState, newState)
            when {
                transitionType == StackTransitionType.Replace -> {
                    scaleIn(initialScale = 2f) + fadeIn() togetherWith fadeOut()
                }
                oldState?.stack?.last() is DialogScreen -> {
                    fadeIn() togetherWith fadeOut()
                }
                oldState?.stack?.last() !is DialogScreen && newState?.stack?.last() is DialogScreen -> {
                    fadeIn() togetherWith fadeOut()
                }
                else -> {
                    val (initialOffset, targetOffset) = when (transitionType) {
                        StackTransitionType.Pop -> ({ size: Int -> -size }) to ({ size: Int -> size })
                        else -> ({ size: Int -> size }) to ({ size: Int -> -size })
                    }
                    slideInHorizontally(initialOffsetX = initialOffset, animationSpec = tween(durationMillis = 1000)) togetherWith
                        slideOutHorizontally(targetOffsetX = targetOffset, animationSpec = tween(durationMillis = 1000))
                }
            }
        }
    )
}