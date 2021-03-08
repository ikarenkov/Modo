package com.github.terrakok.modo

/**
 * Main reducer with logic for basic navigation actions
 */
class ModoReducer : NavigationReducer {
    override fun invoke(action: NavigationAction, state: NavigationState): NavigationState =
        when (action) {
            is Forward -> NavigationState(
                state.chain + listOf(action.screen, *action.screens)
            )
            is Replace -> NavigationState(
                state.chain.dropLast(1) + listOf(action.screen, *action.screens)
            )
            is NewStack -> NavigationState(
                listOf(action.screen, *action.screens)
            )
            is BackTo -> {
                val i = state.chain.indexOfLast { it.id == action.screenId }
                if (i != -1) NavigationState(state.chain.take(i + 1))
                else state
            }
            is BackToRoot -> NavigationState(
                listOfNotNull(state.chain.firstOrNull())
            )
            is Back -> NavigationState(
                state.chain.dropLast(1)
            )
            is Exit -> NavigationState()
            else -> state
        }
}