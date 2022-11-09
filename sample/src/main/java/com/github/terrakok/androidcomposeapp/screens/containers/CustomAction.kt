package com.github.terrakok.androidcomposeapp.screens.containers

import com.github.terrakok.modo.*
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.multiscreen.MultiReducer
import com.github.terrakok.modo.multiscreen.MultiScreenState

class AddTab(
    val id: String,
    val rootScreen: Screen
) : NavigationAction

class CustomReducer : NavigationReducer<MultiScreenState> {
    private val multiReducer = MultiReducer()

    override fun reduce(action: NavigationAction, state: MultiScreenState): MultiScreenState {
        if (action is AddTab) {
            return MultiScreenState(
                state.containers + SampleStack(action.rootScreen),
                state.selected
            )
        }
        return multiReducer.reduce(action, state)
    }

}