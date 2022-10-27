package com.github.terrakok.androidcomposeapp

import com.github.terrakok.modo.*
import com.github.terrakok.modo.Screen

class AddTab(
    val id: String,
    val rootScreen: Screen
) : NavigationAction

class CustomReducer : NavigationReducer<MultiNavigation> {
    private val multiReducer = MultiReducer()

    override fun reduce(action: NavigationAction, state: MultiNavigation): MultiNavigation {
        if (action is AddTab) {
            return MultiNavigation(
                state.containers + SampleStack(action.rootScreen),
                state.selected
            )
        }
        return multiReducer.reduce(action, state)
    }

}