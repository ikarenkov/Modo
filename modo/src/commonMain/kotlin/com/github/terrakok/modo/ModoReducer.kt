package com.github.terrakok.modo

/**
 * Main reducer with logic for basic navigation actions
 */
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
                val i = state.chain.indexOfLast { it.id == action.screenId }
                if (i != -1) NavigationState(state.chain.take(i + 1))
                else state
            }
            is Exit -> NavigationState()
            else -> state
        }
}