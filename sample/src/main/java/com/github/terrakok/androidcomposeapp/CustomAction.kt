package com.github.terrakok.androidcomposeapp

import com.github.terrakok.modo.*
import com.github.terrakok.modo.android.compose.ComposeScreen
import com.github.terrakok.modo.android.compose.Stack

class AddTab(
    val id: String,
    val rootScreen: ComposeScreen
) : NavigationAction

class CustomReducer : NavigationReducer {
    private val multiReducer = MultiReducer()

    override fun reduce(action: NavigationAction, state: NavigationState): NavigationState? {
        if (state is MultiNavigation && action is AddTab) {
            return MultiNavigation(
                state.containers + Stack(action.id, action.rootScreen),
                state.selected
            )
        }
        return multiReducer.reduce(action, state)
    }

}