package com.github.terrakok.modo

interface ScreenId {
    val id: String
}

interface NavigationAction
interface NavigationState

interface NavigationReducer {
    fun reduce(action: NavigationAction, state: NavigationState): NavigationState?
}

interface NavigationDispatcher {
    fun dispatch(action: NavigationAction)
}

open class Screen(
    override val id: String,
    val container: NavigationDispatcher?
) : ScreenId, NavigationDispatcher {
    override fun dispatch(action: NavigationAction) {
        container?.dispatch(action)
            ?: throw IllegalStateException("Screen `$id` should have container!")
    }
}

open class ContainerScreen(
    id: String,
    initialState: NavigationState,
    private val reducer: NavigationReducer,
    outerContainer: NavigationDispatcher? = null
) : Screen(id, outerContainer) {

    var navigationState: NavigationState = initialState
        private set(value) {
            field = value
            onStateUpdate(value)
        }

    override fun dispatch(action: NavigationAction) {
        val newState = reducer.reduce(action, navigationState)
        if (newState != null) {
            navigationState = newState
        } else if (container != null) {
            container.dispatch(action)
        } else {
            throw IllegalStateException("Unknown action $action in root screen `$id`!")
        }
    }

    open fun onStateUpdate(state: NavigationState) {}
}
