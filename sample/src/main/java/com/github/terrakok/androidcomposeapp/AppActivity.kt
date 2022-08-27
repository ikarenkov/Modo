package com.github.terrakok.androidcomposeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.github.terrakok.modo.Modo
import com.github.terrakok.modo.ComposeRenderer
import com.github.terrakok.modo.ScreenTransition
import com.github.terrakok.modo.ScreenTransitionType
import com.github.terrakok.modo.back

class AppActivity : ComponentActivity() {
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val render = ComposeRenderer {
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
        Modo.init(SampleScreen(1))
        Modo.setRenderer(render)
        setContent {
            BackHandler { Modo.navigator.back() }
            Surface(color = MaterialTheme.colors.background) { render.Content() }
        }
    }
}
