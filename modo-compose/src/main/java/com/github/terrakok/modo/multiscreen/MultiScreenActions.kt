package com.github.terrakok.modo.multiscreen

import com.github.terrakok.modo.NavigationAction
import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.ReducerAction

interface MultiScreenAction : NavigationAction<MultiScreenState>
fun interface MultiScreenReducerAction : MultiScreenAction, ReducerAction<MultiScreenState>

class SetContainers(val state: MultiScreenState) : MultiScreenReducerAction {
    override fun reduce(oldState: MultiScreenState): MultiScreenState =
        state
}

class SelectContainer(private val index: Int) : MultiScreenReducerAction {
    override fun reduce(oldState: MultiScreenState): MultiScreenState =
        oldState.copy(selected = index)
}

fun MultiScreenNavContainer.dispatch(action: (MultiScreenState) -> MultiScreenState) = dispatch(MultiScreenReducerAction(action))

fun NavigationContainer<MultiScreenState, MultiScreenAction>.setContainers(state: MultiScreenState) = dispatch(SetContainers(state))
fun NavigationContainer<MultiScreenState, MultiScreenAction>.selectContainer(index: Int) = dispatch(SelectContainer(index))