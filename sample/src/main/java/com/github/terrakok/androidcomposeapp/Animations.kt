package com.github.terrakok.androidcomposeapp

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.runtime.Composable
import com.github.terrakok.modo.ComposeRendererScope
import com.github.terrakok.modo.animation.ScreenTransition
import com.github.terrakok.modo.animation.StackTransitionType
import com.github.terrakok.modo.animation.calculateStackTransitionType
import com.github.terrakok.modo.stack.StackState

@Composable
@OptIn(ExperimentalAnimationApi::class)
fun ComposeRendererScope<StackState>.SlideTransition() {
    ScreenTransition(
        transitionSpec = {
            val transitionType = calculateStackTransitionType(oldState, newState)
            if (transitionType == StackTransitionType.Replace) {
                scaleIn(initialScale = 2f) + fadeIn() with fadeOut()
            } else {
                val (initialOffset, targetOffset) = when (transitionType) {
                    StackTransitionType.Pop -> ({ size: Int -> -size }) to ({ size: Int -> size })
                    else -> ({ size: Int -> size }) to ({ size: Int -> -size })
                }
                slideInHorizontally(initialOffsetX = initialOffset) with
                    slideOutHorizontally(targetOffsetX = targetOffset)
            }
        }
    )
}