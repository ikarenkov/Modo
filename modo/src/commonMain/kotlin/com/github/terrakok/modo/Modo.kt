package com.github.terrakok.modo

interface Screen {
    val id: String
}

/**
 * Marker for actions which will be applied to state via reducer
 */
interface NavigationAction

/**
 * Holder of current navigation state
 */
data class NavigationState(
    val chain: List<Screen> = emptyList()
)

typealias NavigationReducer = (action: NavigationAction, state: NavigationState) -> NavigationState

interface NavigationRender {
    operator fun invoke(state: NavigationState)
}

fun interface NavigationDispatcher {
    fun dispatch(action: NavigationAction)
}

/**
 * Modo is navigation state holder and dispatcher actions to reducer
 */
class Modo(
    private val reducer: NavigationReducer
): NavigationDispatcher {
    var state = NavigationState()
        internal set(value) {
            field = value
            render?.invoke(field)
        }
    var render: NavigationRender? = null
        set(value) {
            field = value
            field?.invoke(state)
        }

    override fun dispatch(action: NavigationAction) {
        state = reducer(action, state)
    }
}
