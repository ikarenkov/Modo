package com.github.terrakok.androidcomposeapp.screens.containers

import androidx.compose.runtime.Composable
import com.github.terrakok.androidcomposeapp.SlideTransition
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.stack.StackState
import com.github.terrakok.modo.stack.StackNavModel
import com.github.terrakok.modo.stack.StackScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class SampleStack(
    private val stackNavModel: StackNavModel
) : StackScreen(stackNavModel) {

    constructor(initialState: StackState) : this(StackNavModel(initialState))

    constructor(rootScreen: Screen) : this(initialState = StackState(rootScreen))

    @Composable
    override fun Content() {
        TopScreenContent {
            SlideTransition()
        }
    }
}