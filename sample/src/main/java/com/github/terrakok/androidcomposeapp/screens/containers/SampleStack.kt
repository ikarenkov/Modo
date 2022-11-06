package com.github.terrakok.androidcomposeapp.screens.containers

import androidx.compose.runtime.Composable
import com.github.terrakok.androidcomposeapp.SlideTransition
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.containers.StackNavigationState
import com.github.terrakok.modo.containers.NavigationModel
import com.github.terrakok.modo.containers.StackScreen
import kotlinx.parcelize.Parcelize

@Parcelize
class SampleStack(
    private val navigationModel: NavigationModel<StackNavigationState>
) : StackScreen(navigationModel) {

    constructor(initialState: StackNavigationState) : this(NavigationModel(initialState))

    constructor(rootScreen: Screen) : this(initialState = StackNavigationState(rootScreen))

    @Composable
    override fun Content() {
        TopScreenContent {
            SlideTransition()
        }
    }
}