package com.github.terrakok.modo

typealias NavigationReducer = (action: NavigationAction, state: NavigationState) -> NavigationState
typealias NavigationRender = (state: NavigationState) -> Unit

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

class ModoReducer : NavigationReducer {
    override fun invoke(action: NavigationAction, state: NavigationState): NavigationState =
        when (action) {
            is Back -> NavigationState(
                state.chain.dropLast(1)
            )
            is Forward -> NavigationState(
                state.chain.plus(action.screen)
            )
            is Replace -> NavigationState(
                state.chain.dropLast(1).plus(action.screen)
            )
            is NewStack -> NavigationState(
                action.screen.toList()
            )
            is BackTo -> {
                if (action.screen == null) {
                    NavigationState()
                } else {
                    val i = state.chain.indexOfLast { it.id == action.screen.id }
                    if (i != -1) NavigationState(state.chain.take(i + 1))
                    else state
                }
            }
            else -> state
        }
}