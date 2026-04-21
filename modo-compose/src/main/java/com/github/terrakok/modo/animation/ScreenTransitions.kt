package com.github.terrakok.modo.animation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.ComposeRendererScope
import com.github.terrakok.modo.LocalInTransitionContext
import com.github.terrakok.modo.SaveableContent
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.model.lifecycleDependency

val cleanupProtectedScreens = mutableStateMapOf<Screen, Unit>()
val preDisposeProtectedScreens = mutableStateMapOf<Screen, Unit>()

typealias ScreenTransitionContent = @Composable AnimatedVisibilityScope.(Screen) -> Unit

/**
 * The way to animate [Screen]'s changing (transition).
 *
 * It brings changes to the lifecycle of animated the [Screen]:
 * * ON_RESUME - when the screen animation is finished and the screen is displayed
 * * ON_PAUSE - when the screen animation is started and the screen is going to be hidden
 *
 * @param modifier - the modifier for the [AnimatedContent].
 * @param screenModifier - the modifier for the [Screen.Content].
 * @param transitionSpec - the transition spec for the [AnimatedContent].
 * @param content - the content that is going to be placed inside [AnimatedContent]. You can use it to decorate or customize the content.
 * Screens rendered by this transition will automatically have their lifecycle controlled by the animation.
 */
@Suppress("MagicNumber")
@Composable
fun ComposeRendererScope<*>.ScreenTransition(
    modifier: Modifier = Modifier,
    screenModifier: Modifier = Modifier,
    transitionSpec: AnimatedContentTransitionScope<Screen>.() -> ContentTransform = {
        fadeIn(animationSpec = tween(220, delayMillis = 90)) +
            scaleIn(initialScale = 0.92f, animationSpec = tween(220, delayMillis = 90)) togetherWith
            fadeOut(animationSpec = tween(90))
    },
    content: ScreenTransitionContent = { it.SaveableContent(screenModifier) }
) {
    val transition = updateTransition(targetState = screen, label = "ScreenTransition")
    CompositionLocalProvider(LocalInTransitionContext provides true) {
        transition.AnimatedContent(
            transitionSpec = transitionSpec,
            modifier = modifier,
            contentKey = { it.screenKey },
        ) { screen ->
            content(screen)
            DisposableEffect(transition.currentState, transition.targetState) {
                if (screen == transition.currentState && screen != transition.targetState) {
                    // Start of animation that hides this screen, so we should pause lifecycle
                    screen.lifecycleDependency()?.hideTransitionStarted()
                }
                if (transition.currentState == transition.targetState && screen == transition.currentState) {
                    // Finish of animation that shows this screen, so we should resume lifecycle
                    screen.lifecycleDependency()?.showTransitionFinished()
                }
                onDispose { }
            }
        }
    }
}