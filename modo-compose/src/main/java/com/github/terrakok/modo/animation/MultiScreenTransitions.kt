package com.github.terrakok.modo.animation

import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Left
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Right
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import com.github.terrakok.modo.ComposeRendererScope
import com.github.terrakok.modo.SaveableContent
import com.github.terrakok.modo.multiscreen.MultiScreenState

@Composable
fun ComposeRendererScope<MultiScreenState>.SlideTransition(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    slideAnimationSpec: FiniteAnimationSpec<IntOffset> = tween(durationMillis = 700),
    fadeAnimationSpec: FiniteAnimationSpec<Float> = tween(durationMillis = 700),
    content: ScreenTransitionContent = { it.SaveableContent(screenModifier, manualResumePause = true) }
) {
    ScreenTransition(
        modifier = modifier,
        screenModifier = modifier,
        transitionSpec = {
            if (oldState != null && newState != null) {
                if (oldState.selected != newState.selected) {
                    if (oldState.selected < newState.selected) {
                        slideIntoContainer(Left, animationSpec = slideAnimationSpec) togetherWith
                            slideOutOfContainer(Left, animationSpec = slideAnimationSpec)
                    } else {
                        slideIntoContainer(Right, animationSpec = slideAnimationSpec) togetherWith
                            slideOutOfContainer(Right, animationSpec = slideAnimationSpec)
                    }
                } else {
                    fadeIn(fadeAnimationSpec) togetherWith fadeOut(fadeAnimationSpec)
                }
            } else {
                EnterTransition.None togetherWith ExitTransition.None
            }
        },
        content = content
    )
}