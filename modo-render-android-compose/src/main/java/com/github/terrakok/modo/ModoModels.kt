package com.github.terrakok.modo

interface NavigationState {
    fun getChildScreens(): List<Screen>
}

interface NavigationAction

interface NavigationReducer<State : NavigationState> {
    fun reduce(action: NavigationAction, state: State): State
}

fun interface NavigationDispatcher {
    fun dispatch(action: NavigationAction)
}

interface NavigationRenderer {
    fun render(state: NavigationState)
}
