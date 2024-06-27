package com.github.terrakok.modo.sample.screens.animation

import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.animation.ScreenTransition
import com.github.terrakok.modo.animation.StackTransitionType
import com.github.terrakok.modo.animation.calculateStackTransitionType
import com.github.terrakok.modo.stack.StackNavModel
import com.github.terrakok.modo.stack.StackScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class AnimationStackScreen(
    val navModel: StackNavModel = StackNavModel(AnimationScreen())
) : StackScreen(navModel) {

    private fun defaultEnterTransition() = fadeIn()
    private fun defaultExitTransition() = fadeOut()

    @Composable
    override fun Content(modifier: Modifier) {
        TopScreenContent(modifier) { modifier ->
            ScreenTransition(
                modifier = modifier,
                transitionSpec = {
                    val lastNew = newState?.stack?.lastOrNull()
                    val lastNewAnimation = lastNew as? StackAnimation
                    val lastOld = oldState?.stack?.lastOrNull()
                    val lastOldAnimation = lastOld as? StackAnimation
                    val transitionType = calculateStackTransitionType()
                    val animationScope = this
                    when (transitionType) {
                        StackTransitionType.Push -> {
                            calculateTransition(
                                appearAnimationScreen = lastNewAnimation,
                                disappearAnimationScreen = lastOldAnimation,
                                transition = { getPushTransition(animationScope) },
                                enterTransition = { getPushEnterTransition(animationScope) },
                                exitTransition = { getPushExitTransition(animationScope) }
                            )
                        }
                        StackTransitionType.Replace -> {
                            calculateTransition(
                                appearAnimationScreen = lastNewAnimation,
                                disappearAnimationScreen = lastOldAnimation,
                                transition = { getReplaceTransition(animationScope) },
                                enterTransition = { getReplaceEnterTransition(animationScope) },
                                exitTransition = { getReplaceExitTransition(animationScope) }
                            )
                        }
                        StackTransitionType.Pop -> {
                            calculateTransition(
                                appearAnimationScreen = lastNewAnimation,
                                disappearAnimationScreen = lastOldAnimation,
                                transition = { getPopTransition(animationScope) },
                                enterTransition = { getPopEnterTransition(animationScope) },
                                exitTransition = { getPopExitTransition(animationScope) }
                            )
                        }
                        StackTransitionType.Idle -> EnterTransition.None togetherWith ExitTransition.None
                    }
                }
            )
        }
    }

    fun calculateTransition(
        appearAnimationScreen: StackAnimation?,
        disappearAnimationScreen: StackAnimation?,
        transition: StackAnimation.() -> ContentTransform?,
        enterTransition: StackAnimation.() -> EnterTransition?,
        exitTransition: StackAnimation.() -> ExitTransition?
    ) = appearAnimationScreen?.transition()
        ?: (
            (appearAnimationScreen?.enterTransition() ?: defaultEnterTransition()) togetherWith
                (disappearAnimationScreen?.exitTransition() ?: defaultExitTransition())
            )
}