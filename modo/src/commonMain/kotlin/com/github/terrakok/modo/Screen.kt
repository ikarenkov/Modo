package com.github.terrakok.modo

interface Screen {
    val screenName: String get() = "Screen"
}

open class ContainerScreen<State : NavigationState>(
    initialState: State,
    private val reducer: NavigationReducer<State>
) : Screen, NavigationDispatcher {

    open var navigationState: State = initialState
        set(value) {
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
//            container?.dispatch(action)
        }
    }

}