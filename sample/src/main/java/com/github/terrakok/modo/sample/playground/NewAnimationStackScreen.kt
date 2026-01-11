package com.github.terrakok.modo.sample.playground

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.SaveableContent
import com.github.terrakok.modo.sample.playground.animation.PredictiveBackStackAnimationPOCV2
import com.github.terrakok.modo.sample.playground.animation.StackAnimation
import com.github.terrakok.modo.sample.playground.animation.StackAnimator
import com.github.terrakok.modo.sample.playground.animation.slide
import com.github.terrakok.modo.sample.screens.MainScreen
import com.github.terrakok.modo.stack.StackBackHandler
import com.github.terrakok.modo.stack.StackNavModel
import com.github.terrakok.modo.stack.StackScreenNew
import com.github.terrakok.modo.stack.back
import kotlinx.parcelize.Parcelize

/**
 * Example implementation using the new customizable animation system.
 *
 * Demonstrates how to use StackAnimation with custom animators.
 * Users can easily switch between different animation styles.
 */
@Parcelize
class NewAnimationStackScreen(
    private val navModel: StackNavModel = StackNavModel(MainScreen(0)),
    private val predictiveBack: Boolean = true
) : StackScreenNew(navModel) {

    /**
     * Override this to customize the animator.
     * Examples:
     * - fade() + slide() (default)
     * - iosLikeAnimation()
     * - materialAnimation()
     * - customAnimator()
     */
    open val animator: StackAnimator
        get() = slide()

    @OptIn(ExperimentalModoApi::class)
    @Composable
    override fun Content(modifier: Modifier) {
        StackBackHandler()
        Content(modifier) {
            if (predictiveBack) {
                PredictiveBackStackAnimationPOCV2(
                    modifier = modifier,
                    animator = animator,
                    animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                    onBack = { navModel.back() },
                    content = { screen ->
                        screen.SaveableContent(manualResumePause = true)
                    }
                )
            } else {
                navigationState.StackAnimation(
                    modifier = modifier,
                    animator = animator,
                    animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
                    content = { screen ->
                        screen.SaveableContent(manualResumePause = true)
                    }
                )
            }
        }
    }

}