package com.github.terrakok.androidcomposeapp

import androidx.compose.foundation.background
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

fun Modifier.randomBackground() = composed {
    val backgroundColorIndex = rememberSaveable { Random.nextInt(0, Colors.size) }
    val backgroundColor = remember { Colors[backgroundColorIndex] }
    then(Modifier.background(backgroundColor))
}

val Colors = listOf(
    Color.White,
    Color.Blue,
    Color.Gray,
    Color.Cyan,
    Color.Green,
    Color.Magenta,
    Color.Red,
    Color.Yellow,
)