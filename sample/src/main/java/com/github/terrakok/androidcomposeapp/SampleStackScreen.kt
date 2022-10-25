package com.github.terrakok.androidcomposeapp

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.android.compose.LocalContainerScreen
import com.github.terrakok.modo.android.compose.ScreenTransition
import com.github.terrakok.modo.android.compose.ScreenTransitionType
import com.github.terrakok.modo.android.compose.Stack
import com.github.terrakok.modo.android.compose.generateScreenKey
import com.github.terrakok.modo.back

class SampleStackScreen(
    private val i: Int,
    override val screenKey: String = generateScreenKey()
) : Stack(SampleScreen(1)) {
    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    override fun Content() {
        val parent = LocalContainerScreen.current
        Column(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.padding(2.dp)) {
                Text("Container $i")
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    modifier = Modifier.clickable {
                        parent.back()
                    },
                    text = "[X]"
                )
            }
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .background(Color.Gray)
                    .padding(2.dp)
                    .background(Color.White)
            ) {
                TopScreenContent {
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
            }
        }
    }
}

@Preview
@Composable
private fun PreviewContainerScreen() {
    SampleStackScreen(1).Content()
}