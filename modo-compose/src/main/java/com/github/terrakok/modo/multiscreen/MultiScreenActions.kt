package com.github.terrakok.modo.multiscreen

import com.github.terrakok.modo.NavigationAction
import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.ReducerAction

interface MultiScreenAction : NavigationAction<MultiScreenState>
fun interface MultiScreenReducerAction : MultiScreenAction, ReducerAction<MultiScreenState>

@Deprecated(
    message = "Class with this name was renamed to SelectScreen. This typealias will be removed in further releases.",
    replaceWith = ReplaceWith("SetMultiScreenState")
)
typealias SetContainers = SetMultiScreenState

class SetMultiScreenState(val state: MultiScreenState) : MultiScreenReducerAction {
    override fun reduce(oldState: MultiScreenState): MultiScreenState =
        state
}

@Deprecated(
    message = "Class with this name was renamed to SelectScreen. This typealias will be removed in further releases.",
    replaceWith = ReplaceWith("SelectScreen")
)
typealias SelectContainer = SelectScreen

class SelectScreen(private val pos: Int) : MultiScreenReducerAction {
    override fun reduce(oldState: MultiScreenState): MultiScreenState =
        oldState.copy(selected = pos)
}

fun MultiScreenNavContainer.dispatch(action: (MultiScreenState) -> MultiScreenState) = dispatch(MultiScreenReducerAction(action))

@Deprecated(
    message = "This function was renamed to setState. This function will be removed in further releases.",
    replaceWith = ReplaceWith("setState(state)")
)
fun NavigationContainer<MultiScreenState, MultiScreenAction>.setContainers(state: MultiScreenState) = setState(state)
@Deprecated(
    message = "This function was renamed to selectScreen. This function will be removed in further releases.",
    replaceWith = ReplaceWith("selectScreen(index)")
)

fun NavigationContainer<MultiScreenState, MultiScreenAction>.selectContainer(index: Int) = selectScreen(index)

fun NavigationContainer<MultiScreenState, MultiScreenAction>.setState(state: MultiScreenState) = dispatch(SetMultiScreenState(state))

fun NavigationContainer<MultiScreenState, MultiScreenAction>.selectScreen(pos: Int) = dispatch(SelectScreen(pos))