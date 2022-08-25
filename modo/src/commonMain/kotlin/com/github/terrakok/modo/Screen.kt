package com.github.terrakok.modo

interface Screen {
    val id: String
}

// FIXME: why do we need some more abstractions?
class Navigator(val dispatch: (action: NavigationAction) -> Unit)

val Screen.container get() = Modo.findScreenContainer(this)
val Screen.navigator get() = Navigator {
    if (this is ContainerScreen<*>) dispatch(it) else container?.dispatch(it)
}

open class ContainerScreen<State: NavigationState>(
    override val id: String,
    initialState: State,
    private val reducer: NavigationReducer<State>
) : Screen, NavigationDispatcher {

    var navigationState: State = initialState
        private set(value) {
            field = value
            renderer?.render(value)
        }

    var renderer: NavigationRenderer? = null
        set(value) {
            field = value
            value?.render(navigationState)
        }

    override fun dispatch(action: NavigationAction) {
        val newState = reducer.reduce(action, navigationState)
        if (newState != null) {
            navigationState = newState
        } else {
            container?.dispatch(action)
        }
    }
}