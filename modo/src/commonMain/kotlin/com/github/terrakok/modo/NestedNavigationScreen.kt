package com.github.terrakok.modo

interface NestedNavigationScreen : Screen {
    var navigationState: NavigationState
    val reducer: NavigationReducer
}

/**
 * Nested action wraps another action for [NestedNavigationScreen.reducer]
 */
data class NestedAction(val navigationAction: NavigationAction) : NavigationAction

/**
 * Handles [NestedAction]
 */
class NestedNavigationReducer(
    private val origin: NavigationReducer,
    private val backOnEmptyInnerState: Boolean = true
) : NavigationReducer {

    override fun invoke(action: NavigationAction, state: NavigationState): NavigationState = when (action) {
        is NestedAction -> {
            val wrapperScreen = state.chain.last()
            if (wrapperScreen is NestedNavigationScreen) {
                val newInnerState = wrapperScreen.reducer(action.navigationAction, wrapperScreen.navigationState)
                if (backOnEmptyInnerState && newInnerState.chain.isEmpty()) {
                    origin.invoke(Back, state)
                } else {
                    wrapperScreen.navigationState = newInnerState
                    state
                }
            } else {
                origin(action.navigationAction, state)
            }
        }
        else -> origin(action, state)
    }

}