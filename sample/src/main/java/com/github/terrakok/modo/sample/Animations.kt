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
import androidx.compose.ui.unit.IntOffset
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
            val transitionType = calculateStackTransitionType()
            when {
                transitionType == StackTransitionType.Replace -> {
                    val animationSpec = tween<Float>(durationMillis = SampleAppConfig.animationDurationMs)
                    scaleIn(initialScale = 2f, animationSpec = animationSpec) + fadeIn(animationSpec) togetherWith
                        fadeOut(animationSpec)
                }
                screen is DialogScreen -> {
                    val animationSpec = tween<Float>(durationMillis = SampleAppConfig.animationDurationMs)
                    fadeIn(animationSpec) togetherWith fadeOut(animationSpec)
                }
                else -> {
                    val (initialOffset, targetOffset) = when (transitionType) {
                        StackTransitionType.Pop -> ({ size: Int -> -size }) to ({ size: Int -> size })
                        else -> ({ size: Int -> size }) to ({ size: Int -> -size })
                    }
                    val animationSpec = tween<IntOffset>(durationMillis = SampleAppConfig.animationDurationMs)
                    slideInHorizontally(initialOffsetX = initialOffset, animationSpec = animationSpec) togetherWith
                        slideOutHorizontally(targetOffsetX = targetOffset, animationSpec = animationSpec)
                }
            }
        }
    )
}