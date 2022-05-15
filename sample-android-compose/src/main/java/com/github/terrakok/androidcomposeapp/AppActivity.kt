package com.github.terrakok.androidcomposeapp

import android.os.Bundle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.android.compose.*
import com.github.terrakok.modo.back
import com.github.terrakok.modo.format

class AppActivity : AppCompatActivity() {
    private val modo get() = App.INSTANCE.modo

    @OptIn(ExperimentalAnimationApi::class)
    private val render = ComposeRenderImpl(this) {
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        modo.init(savedInstanceState) { SampleScreen(1) }
        setContent {
            BackHandler {
                modo.back()
            }
            CompositionLocalProvider(
                LocalModo provides modo
            ) {
                Surface(color = MaterialTheme.colors.background) {
                    Column {
                        val scrollState = rememberScrollState()
                        val text = render.state.value.format()
                        LaunchedEffect(text) {
                            scrollState.animateScrollTo(scrollState.maxValue)
                        }
                        Text(
                            text = text,
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .background(Color.Black)
                                .padding(4.dp)
                                .verticalScroll(scrollState),
                            color = Color.White.copy(alpha = 0.5f)
                        )
                        Box(modifier = Modifier.weight(3f)) {
                            render.Content()
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        modo.render = render
    }

    override fun onPause() {
        super.onPause()
        modo.render = null
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        modo.saveState(outState)
    }

}
