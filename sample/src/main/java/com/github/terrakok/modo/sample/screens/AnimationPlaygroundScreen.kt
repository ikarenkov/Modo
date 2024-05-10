package com.github.terrakok.modo.sample.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.updateTransition
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey
import com.github.terrakok.modo.animation.StackTransitionType
import com.github.terrakok.modo.generateScreenKey
import com.github.terrakok.modo.sample.randomColor
import kotlinx.parcelize.Parcelize
import java.util.UUID

@Parcelize
class AnimationPlaygroundScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {
    @Composable
    override fun Content(modifier: Modifier) {
        val stack = remember {
            mutableStateListOf(Item(Color.Yellow))
        }
        var lastTransitionType by remember {
            mutableStateOf(StackTransitionType.Idle)
        }
        Column(
            Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row {
                Button(
                    onClick = {
                        stack += Item(randomColor())
                        lastTransitionType = StackTransitionType.Push
                    }
                ) {
                    Text("Push")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (stack.isNotEmpty()) {
                        stack.removeLast()
                        lastTransitionType = StackTransitionType.Pop
                    }
                }) {
                    Text("Pop")
                }
            }
            Box(
                Modifier
                    .weight(1f)
                    .background(Color.White)
                    .padding(vertical = 16.dp)
            ) {
                val lastColor = stack.lastOrNull()
                val transition = updateTransition(targetState = lastColor, label = "ColorTestTransition")
                transition.AnimatedContent(
                    transitionSpec = { getTransitionSpec(lastTransitionType) },
                    contentKey = { it?.id },
                    modifier = Modifier.size(200.dp)
                ) { item ->
                    if (item != null) {
                        Box(
                            modifier = Modifier
                                .background(item.color)
                                .fillMaxSize()
                                .border(2.dp, Color.Gray)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        )
                    }
                }
            }
        }
    }

    data class Item(
        val color: Color,
        val id: String = UUID.randomUUID().toString()
    )
}

fun getTransitionSpec(transitionType: StackTransitionType): ContentTransform {
    val (initialOffset, targetOffset) = when (transitionType) {
        StackTransitionType.Pop -> ({ size: Int -> -size }) to ({ size: Int -> size })
        else -> ({ size: Int -> size }) to ({ size: Int -> -size })
    }
    return slideInHorizontally(initialOffsetX = initialOffset) togetherWith
        slideOutHorizontally(targetOffsetX = targetOffset)
}