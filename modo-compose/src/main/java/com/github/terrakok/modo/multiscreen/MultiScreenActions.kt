package com.github.terrakok.modo.multiscreen

import com.github.terrakok.modo.NavigationAction
import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.ReducerAction

@Deprecated("Use MultiScreenReducer instead.", ReplaceWith("MultiScreenReducer"))
typealias MultiScreenReducerAction = MultiScreenReducer

fun interface MultiScreenReducer : NavigationReducer<MultiScreenState>

class SetMultiScreenState(val state: MultiScreenState) : MultiScreenReducer {
    override fun reduce(oldState: MultiScreenState): MultiScreenState =
        state
}

@Deprecated(
    message = "Class with this name was renamed to SelectScreen. This typealias will be removed in further releases.",
    replaceWith = ReplaceWith("SelectScreen")
)
typealias SelectContainer = SelectScreen

class SelectScreen(private val pos: Int) : MultiScreenReducer {
    override fun reduce(oldState: MultiScreenState): MultiScreenState =
        oldState.copy(selected = pos)
}

fun MultiScreenNavContainer.dispatch(action: (MultiScreenState) -> MultiScreenState) = dispatch(NavigationReducer(action))

@Deprecated(
    message = "This function was renamed to setState. This function will be removed in further releases.",
    replaceWith = ReplaceWith("setState(state)")
)
fun NavigationContainer<MultiScreenState>.setContainers(state: MultiScreenState) = setState(state)

@Deprecated(
    message = "This function was renamed to selectScreen. This function will be removed in further releases.",
    replaceWith = ReplaceWith("selectScreen(index)")
)
fun NavigationContainer<MultiScreenState>.selectContainer(index: Int) = selectScreen(index)

fun NavigationContainer<MultiScreenState>.setState(state: MultiScreenState) = dispatch(SetMultiScreenState(state))

fun NavigationContainer<MultiScreenState>.selectScreen(pos: Int) = dispatch(SelectScreen(pos))