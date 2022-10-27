package com.github.terrakok.modo.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.ComposeRendererScope
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.SaveableContent
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.StackNavigationState

enum class ScreenTransitionType {
    Push,
    Replace,
    Pop,
    Idle
}

typealias ScreenTransitionContent = @Composable AnimatedVisibilityScope.(Screen) -> Unit

@ExperimentalAnimationApi
@Composable
fun ComposeRendererScope.ScreenTransition(
    modifier: Modifier = Modifier,
    transitionSpec: AnimatedContentScope<Screen>.() -> ContentTransform = {
        fadeIn(animationSpec = tween(220, delayMillis = 90)) +
            scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90)) with
            fadeOut(animationSpec = tween(90))
    },
    content: ScreenTransitionContent = { it.SaveableContent() }
) {
    AnimatedContent(
        targetState = screen,
        transitionSpec = transitionSpec,
        modifier = modifier
    ) {
        content(it)
    }
}

fun defaultCalculateTransitionType(oldScreensState: NavigationState?, newScreensState: NavigationState?): ScreenTransitionType =
    if (oldScreensState is StackNavigationState && newScreensState is StackNavigationState) {
        val oldStack = oldScreensState.stack
        val newStack = newScreensState.stack
        when {
            oldStack.lastOrNull() == newStack.lastOrNull() || oldStack.isEmpty() -> ScreenTransitionType.Idle
            newStack.lastOrNull() in oldStack -> ScreenTransitionType.Pop
            oldStack.lastOrNull() in newStack -> ScreenTransitionType.Push
            else -> ScreenTransitionType.Replace
        }
    } else {
        // TODO: support animation for different type of navigation states
        ScreenTransitionType.Idle
    }