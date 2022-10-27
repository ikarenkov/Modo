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
import com.github.terrakok.modo.animation.ScreenTransitionType

@Composable
@OptIn(ExperimentalAnimationApi::class)
fun ComposeRendererScope.SlideTransition() {
    ScreenTransition(
        transitionSpec = {
            if (transitionType == ScreenTransitionType.Replace) {
                scaleIn(initialScale = 2f) + fadeIn() with fadeOut()
            } else {
                val (initialOffset, targetOffset) = when (transitionType) {
                    ScreenTransitionType.Pop -> ({ size: Int -> -size }) to ({ size: Int -> size })
                    else -> ({ size: Int -> size }) to ({ size: Int -> -size })
                }
                slideInHorizontally(initialOffsetX = initialOffset) with
                    slideOutHorizontally(targetOffsetX = targetOffset)
            }
        }
    )
}