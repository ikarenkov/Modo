package com.github.terrakok.modo

import androidx.compose.runtime.Composable

interface Screen {
    val id: String
    @Composable fun Content()
}

/**
 * Delegate for dispatching navigation calls from Compose Screens
 */
class Navigator(val dispatch: (action: NavigationAction) -> Unit)

val Screen.container get() = Modo.findScreenContainer(this)
val Screen.navigator get() = Navigator {
    if (this is ContainerScreen<*>) dispatch(it) else container?.dispatch(it)
}

interface NavigationState {
    fun getAllScreens(): List<Screen>
    fun getActiveScreen(): Screen?
}

interface NavigationAction

interface NavigationReducer<State: NavigationState> {
    fun reduce(action: NavigationAction, state: State): State?
}

interface NavigationDispatcher {
    fun dispatch(action: NavigationAction)
}