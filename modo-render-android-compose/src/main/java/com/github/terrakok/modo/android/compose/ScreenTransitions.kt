package com.github.terrakok.modo.android.compose

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.Screen

enum class ScreenTransitionType {
    Push,
    Replace,
    Pop,
    Idle
}

typealias ScreenTransitionContent = @Composable AnimatedVisibilityScope.(ComposeScreen) -> Unit

@ExperimentalAnimationApi
@Composable
fun ComposeRendererScope.ScreenTransition(
    modifier: Modifier = Modifier,
    transitionSpec: AnimatedContentScope<ComposeScreen>.() -> ContentTransform = {
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

fun defaultCalculateTransitionType(oldScreensStack: List<Screen>, newScreensStack: List<Screen>): ScreenTransitionType {
    return when {
        oldScreensStack.lastOrNull() == newScreensStack.lastOrNull() || oldScreensStack.isEmpty() -> ScreenTransitionType.Idle
        newScreensStack.lastOrNull() in oldScreensStack -> ScreenTransitionType.Pop
        oldScreensStack.lastOrNull() in newScreensStack -> ScreenTransitionType.Push
        else -> ScreenTransitionType.Replace
    }
}