package com.github.terrakok.modo

typealias NavigationReducer = (action: NavigationAction, state: NavigationState) -> NavigationState

fun interface NavigationRender {

    operator fun invoke(state: NavigationState)

}

fun interface ModoDispatcher {

    fun dispatch(action: NavigationAction)

}

/**
 * Modo is navigation state holder and dispatcher actions to reducer
 */
open class Modo(
    private val reducer: NavigationReducer
): ModoDispatcher {
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
