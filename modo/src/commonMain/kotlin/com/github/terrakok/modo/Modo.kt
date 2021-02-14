package com.github.terrakok.modo

typealias NavigationReducer = (action: NavigationAction, state: NavigationState) -> NavigationState
typealias NavigationRender = (state: NavigationState) -> Unit

/**
 * Modo is navigation state holder and dispatcher actions to reducer
 */
class Modo(
    private val reducer: NavigationReducer
) {
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

    fun dispatch(action: NavigationAction) {
        state = reducer(action, state)
    }
}
