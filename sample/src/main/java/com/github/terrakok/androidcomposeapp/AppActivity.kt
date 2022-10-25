package com.github.terrakok.androidcomposeapp

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import com.github.terrakok.modo.android.compose.ComposeScreen
import com.github.terrakok.modo.android.compose.Modo
import com.github.terrakok.modo.android.compose.ScreenTransition
import com.github.terrakok.modo.android.compose.ScreenTransitionType
import com.github.terrakok.modo.android.compose.Stack
import com.github.terrakok.modo.back

class AppActivity : AppCompatActivity() {

    private lateinit var rootScreen: Stack

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootScreen = Modo.restoreInstanceIfCan<Stack>(savedInstanceState, ::provideRootScreen)
        setContent {
            BackHandler { rootScreen.back() }
            Surface(color = MaterialTheme.colors.background) {
                rootScreen.Content()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        Modo.saveInstanceState(outState, rootScreen)
        super.onSaveInstanceState(outState)
    }

    @OptIn(ExperimentalAnimationApi::class)
    fun provideRootScreen(): Stack = Stack(SampleScreen(1))
//    {
//        ScreenTransition(
//            transitionSpec = {
//                if (transitionType == ScreenTransitionType.Replace) {
//                    scaleIn(initialScale = 2f) + fadeIn() with fadeOut()
//                } else {
//                    val (initialOffset, targetOffset) = when (transitionType) {
//                        ScreenTransitionType.Pop -> ({ size: Int -> -size }) to ({ size: Int -> size })
//                        else -> ({ size: Int -> size }) to ({ size: Int -> -size })
//                    }
//                    slideInHorizontally(initialOffsetX = initialOffset) with
//                        slideOutHorizontally(targetOffsetX = targetOffset)
//                }
//            }
//        )
//    }

}
