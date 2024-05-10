package com.github.terrakok.modo.sample.screens.containers

import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.multiscreen.MultiScreenReducerAction
import com.github.terrakok.modo.multiscreen.MultiScreenState

class AddTab(
    val id: String,
    val rootScreen: Screen
) : MultiScreenReducerAction {
    override fun reduce(oldState: MultiScreenState): MultiScreenState {
        return MultiScreenState(
            oldState.screens + SampleStack(rootScreen),
            oldState.selected
        )
    }
}