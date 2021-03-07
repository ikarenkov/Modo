package com.github.terrakok.modo

data class MultiScreen(
    override val id: String,
    val stacks: List<NavigationState>,
    val selectedStack: Int
) : Screen

data class SelectStack(val stackIndex: Int) : NavigationAction

fun Modo.selectStack(stackIndex: Int) = dispatch(SelectStack(stackIndex))

class MultiReducer(
    private val origin: ModoReducer = ModoReducer()
) : NavigationReducer {
    override fun invoke(action: NavigationAction, state: NavigationState): NavigationState {
        val currentScreen = state.chain.lastOrNull()
        return if (currentScreen is MultiScreen && action is SelectStack) {
            val newScreen = currentScreen.copy(selectedStack = action.stackIndex)
            origin(Replace(newScreen), state)
        } else if (currentScreen is MultiScreen && standardActionIsApplicable(
                currentScreen,
                action
            )
        ) {
            val newScreen = applyOriginToMultiScreen(action, currentScreen)
            origin(Replace(newScreen), state)
        } else {
            origin(action, state)
        }
    }

    private fun standardActionIsApplicable(
        multiScreen: MultiScreen,
        action: NavigationAction
    ): Boolean {
        if (action is Forward || action is Replace) return true
        if (action is Back) {
            return multiScreen.stacks[multiScreen.selectedStack].chain.size > 1
        }
        if (action is BackTo) {
            val localState = multiScreen.stacks[multiScreen.selectedStack]
            return localState.chain.any { it.id == action.screenId }
        }
        return false
    }

    private fun applyOriginToMultiScreen(
        action: NavigationAction,
        multiScreen: MultiScreen
    ): MultiScreen {
        val localState = multiScreen.stacks[multiScreen.selectedStack]
        val newLocalState = origin(action, localState)

        val newStacks = multiScreen.stacks.toMutableList()
        newStacks[multiScreen.selectedStack] = newLocalState

        return multiScreen.copy(stacks = newStacks)
    }
}