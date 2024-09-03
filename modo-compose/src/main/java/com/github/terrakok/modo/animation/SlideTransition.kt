package com.github.terrakok.modo.animation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Down
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.End
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Left
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Right
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Start
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection.Companion.Up
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import com.github.terrakok.modo.ComposeRendererScope
import com.github.terrakok.modo.DialogScreen
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.SaveableContent
import com.github.terrakok.modo.stack.StackState

@Composable
@OptIn(ExperimentalModoApi::class)
fun ComposeRendererScope<StackState>.SlideTransition(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    pushDirection: AnimatedContentTransitionScope.SlideDirection = Left,
    popDirection: AnimatedContentTransitionScope.SlideDirection = pushDirection.opposite(),
    animationSpec: FiniteAnimationSpec<IntOffset> = tween(durationMillis = 1000),
    content: ScreenTransitionContent = { it.SaveableContent(screenModifier, manualResumePause = true) }
) {
    ScreenTransition(
        modifier = modifier,
        screenModifier = screenModifier,
        transitionSpec = {
            val transitionType: StackTransitionType = calculateStackTransitionType()
            when {
                transitionType == StackTransitionType.Replace -> {
                    scaleIn(initialScale = 2f) + fadeIn() togetherWith fadeOut()
                }
                oldState?.stack?.last() is DialogScreen || newState?.stack?.last() is DialogScreen -> {
                    fadeIn() togetherWith fadeOut()
                }
                else -> {
                    when (transitionType) {
                        StackTransitionType.Push -> slideIntoContainer(pushDirection, animationSpec = animationSpec) togetherWith
                            slideOutOfContainer(pushDirection, animationSpec = animationSpec)
                        else -> slideIntoContainer(popDirection, animationSpec = animationSpec) togetherWith
                            slideOutOfContainer(popDirection, animationSpec = animationSpec)
                    }
                }
            }
        },
        content = content
    )
}

private fun AnimatedContentTransitionScope.SlideDirection.opposite() = when (this) {
    Left -> Right
    Right -> Left
    Up -> Down
    Down -> Up
    Start -> End
    End -> Up
    else -> error("Unknown direction $this")
}