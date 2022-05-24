package com.github.terrakok.modo

interface MultiScreen : Screen {
    var multiScreenState: MultiScreenState
    val stacks: List<NavigationState> get() = multiScreenState.stacks
    val selectedStack: Int get() = multiScreenState.selectedStack
}

data class MultiScreenState(
    val stacks: List<NavigationState>,
    val selectedStack: Int
)

class ExternalForward(val screen: Screen, vararg val screens: Screen) : NavigationAction
object BackToLocalRoot : NavigationAction
class SelectStack(val stackIndex: Int) : NavigationAction

fun NavigationDispatcher.externalForward(screen: Screen, vararg screens: Screen) = dispatch(ExternalForward(screen, *screens))
fun NavigationDispatcher.selectStack(stackIndex: Int) = dispatch(SelectStack(stackIndex))
fun NavigationDispatcher.backToLocalRoot() = dispatch(BackToLocalRoot)

class MultiReducer(
    private val origin: NavigationReducer = ModoReducer()
) : NavigationReducer {
    override fun invoke(action: NavigationAction, state: NavigationState) = when (action) {
        is Exit, is BackToRoot, is NewStack -> origin(action, state)
        is ExternalForward -> origin(Forward(action.screen, *action.screens), state)
        is Forward, is Replace, is Back -> applyLocalAction(action, state)
        is BackToLocalRoot -> applyLocalAction(BackToRoot, state)
        is BackTo -> applyBackToAction(action, state) ?: state
        is SelectStack -> applySelectStackAction(action, state)
        else -> applyLocalAction(action, state)
    }

    private fun applyLocalAction(action: NavigationAction, state: NavigationState): NavigationState {
        val localNavigationState = getLocalNavigationState(state)
        val newLocalNavigationState = origin(action, localNavigationState)
        return applyNewLocalNavigationState(state, newLocalNavigationState)
    }

    /**
     * Finds and returns top navigation state, which can be nested in AbstractMultiScreen.
     * In this case it returns deepest NavigationState.
     */
    private fun getLocalNavigationState(state: NavigationState): NavigationState {
        val screen = state.chain.lastOrNull()
        return if (screen is MultiScreen) {
            getLocalNavigationState(screen.multiScreenState.stacks[screen.multiScreenState.selectedStack])
        } else {
            state
        }
    }

    /**
     * Applies new navigation state to the deepest selected multiscreen.
     */
    private fun applyNewLocalNavigationState(state: NavigationState, newLocalNavigationState: NavigationState): NavigationState {
        val screen = state.chain.lastOrNull()
        return if (screen is MultiScreen) {
            val selectedStack = screen.multiScreenState.stacks[screen.multiScreenState.selectedStack]
            val newLocalChain = applyNewLocalNavigationState(selectedStack, newLocalNavigationState)

            if (newLocalChain.chain.isNotEmpty()) {
                val newStacks = screen.multiScreenState.stacks.toMutableList()
                newStacks[screen.multiScreenState.selectedStack] = newLocalChain
                val newMultiScreenState: MultiScreenState = screen.multiScreenState.copy(stacks = newStacks)

                screen.multiScreenState = newMultiScreenState
                state
            } else {
                val newChain = state.chain.dropLast(1)
                NavigationState(newChain)
            }
        } else {
            newLocalNavigationState
        }
    }

    private fun applyBackToAction(action: BackTo, state: NavigationState): NavigationState? {
        val screen = state.chain.lastOrNull() ?: return null
        if (screen is MultiScreen) {
            val newLocalChain = applyBackToAction(action, screen.stacks[screen.selectedStack])
            if (newLocalChain == null) {
                if (state.chain.none { it.id == action.screenId }) return null
                return origin(action, state)
            } else {
                val newStacks = screen.stacks.toMutableList()
                newStacks[screen.selectedStack] = newLocalChain
                screen.multiScreenState = screen.multiScreenState.copy(stacks = newStacks)
                return state
            }
        } else {
            if (state.chain.none { it.id == action.screenId }) return null
            return origin(action, state)
        }
    }

    private fun applySelectStackAction(action: SelectStack, state: NavigationState): NavigationState {
        val screen = state.chain.lastOrNull()
        if (screen is MultiScreen) {
            val selectedStack = screen.stacks[screen.selectedStack]
            if (selectedStack.chain.lastOrNull() is MultiScreen) {
                val newStacks = screen.stacks.toMutableList()
                val newLocalChain = applySelectStackAction(action, selectedStack)
                newStacks[screen.selectedStack] = newLocalChain
                screen.multiScreenState = screen.multiScreenState.copy(stacks = newStacks)
            } else {
                screen.multiScreenState = screen.multiScreenState.copy(selectedStack = action.stackIndex)
            }
        }
        return state
    }
}