package com.github.terrakok.androidcomposeapp

import com.github.terrakok.modo.*
import com.github.terrakok.modo.android.compose.ComposeContainerScreen
import com.github.terrakok.modo.android.compose.ComposeScreen

class AddTab(
    val id: String,
    val rootScreen: ComposeScreen
) : NavigationAction

class CustomReducer : NavigationReducer {
    private val multiReducer = MultiReducer()

    override fun reduce(action: NavigationAction, state: NavigationState): NavigationState? {
        if (state is MultiNavigation && action is AddTab) {
            return MultiNavigation(
                state.containers + ComposeContainerScreen(action.id, action.rootScreen),
                state.activeContainerIndex
            )
        }
        return multiReducer.reduce(action, state)
    }

}