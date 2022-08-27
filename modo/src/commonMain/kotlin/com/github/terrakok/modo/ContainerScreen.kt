package com.github.terrakok.modo

import androidx.compose.runtime.Composable

open class ContainerScreen<State: NavigationState>(
    override val id: String,
    initialState: State,
    private val reducer: NavigationReducer<State>
) : Screen, NavigationDispatcher {

    var navigationState: State = initialState
        private set(value) {
            field = value
            renderer.render(value)
        }

    var renderer: NavigationRenderer = ComposeRenderer(exitAction = { container?.dispatch(Back) })
        set(value) {
            field = value
            value.render(navigationState)
        }

    init {
        renderer.render(navigationState)
    }

    override fun dispatch(action: NavigationAction) {
        val newState = reducer.reduce(action, navigationState)
        if (newState != null) {
            navigationState = newState
        } else {
            container?.dispatch(action)
        }
    }

    @Composable
    final override fun Content() {
        Content(navigationState) { renderer.Content() }
    }

    @Composable
    open fun Content(state: State, screenContent: @Composable () -> Unit) {
        screenContent()
    }
}

open class Stack(id: String, rootScreen: Screen) :
    ContainerScreen<StackNavigation>(id, StackNavigation(listOf(rootScreen)), StackReducer())

open class MultiStack(id: String, vararg rootScreen: Screen, selected: Int) :
    ContainerScreen<MultiNavigation>(
        id,
        MultiNavigation(rootScreen.mapIndexed { i, s -> Stack(i.toString(), s) }, selected),
        MultiReducer()
    )