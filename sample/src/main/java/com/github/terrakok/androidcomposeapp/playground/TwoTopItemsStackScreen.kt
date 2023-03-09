package com.github.terrakok.androidcomposeapp.playground

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.github.terrakok.androidcomposeapp.screens.SampleScreen
import com.github.terrakok.modo.stack.StackNavModel
import com.github.terrakok.modo.stack.StackScreen
import com.github.terrakok.modo.stack.back
import kotlinx.parcelize.Parcelize

// Doesn't work correctly because of bug
@Parcelize
class TwoTopItemsStackScreen(
    private val i: Int,
    private val navModel: StackNavModel = StackNavModel(SampleScreen(i + 1))
) : StackScreen(navModel) {

    @Composable
    override fun Content() {
        BackHandler(enabled = navigationState.stack.size > 1) {
            back()
        }
        Box(Modifier.fillMaxSize()) {
            PreviousScreen()
            CurrentScreen()
        }
    }

    @Composable
    private fun PreviousScreen() {
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }
        Box(
            Modifier
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
                .graphicsLayer(
                    scaleX = 0.4f,
                    scaleY = 0.4f,
                    transformOrigin = TransformOrigin(0f, 0f),
                    translationX = offsetX,
                    translationY = offsetY
                )
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Cyan)
        ) {
            if (navigationState.stack.size > 1) {
                Content(navigationState.stack[navigationState.stack.lastIndex - 1]) {}
            }
        }
    }

    @Composable
    fun CurrentScreen() {
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }
        Box(
            Modifier
                .graphicsLayer(
                    scaleX = 0.8f,
                    scaleY = 0.8f,
                    transformOrigin = TransformOrigin(1f, 1f),
                    translationX = offsetX,
                    translationY = offsetY
                )
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        offsetX += dragAmount.x
                        offsetY += dragAmount.y
                    }
                }
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Green)

        ) {
            TopScreenContent()
        }
    }

}