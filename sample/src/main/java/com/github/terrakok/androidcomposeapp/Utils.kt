package com.github.terrakok.androidcomposeapp

import androidx.compose.foundation.background
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.Color
import kotlin.random.Random

fun Modifier.randomBackground() = composed {
    val backgroundColorInt = rememberSaveable { Random.nextInt() }
    val backgroundColor = remember { Color(backgroundColorInt) }
    then(Modifier.background(backgroundColor))
}