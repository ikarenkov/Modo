package com.github.terrakok.modo

data class MultiScreen(
    override val id: String,
    val stacks: List<NavigationState>,
    val selectedStack: Int
) : Screen

class ExternalForward(val screen: Screen, vararg val screens: Screen) : NavigationAction
object BackToLocalRoot : NavigationAction
class SelectStack(val stackIndex: Int) : NavigationAction

fun Modo.externalForward(screen: Screen, vararg screens: Screen) = dispatch(ExternalForward(screen, *screens))
fun Modo.selectStack(stackIndex: Int) = dispatch(SelectStack(stackIndex))
fun Modo.backToLocalRoot() = dispatch(BackToLocalRoot)

class MultiReducer(
    private val origin: ModoReducer = ModoReducer()
) : NavigationReducer {
    override fun invoke(action: NavigationAction, state: NavigationState) = when (action) {
        is Exit, is BackToRoot, is NewStack -> origin(action, state)
        is ExternalForward -> origin(Forward(action.screen, *action.screens), state)
        is Forward, is Replace, is Back -> applyLocalAction(action, state)
        is BackToLocalRoot -> applyLocalAction(BackToRoot, state)
        is BackTo -> applyBackToAction(action, state) ?: state
        is SelectStack -> applySelectStackAction(action, state)
        else -> state
    }

    private fun applyLocalAction(action: NavigationAction, state: NavigationState): NavigationState {
        val localNavigationState = getLocalNavigationState(state)
        val newLocalNavigationState = origin(action, localNavigationState)
        return applyNewLocalNavigationState(state, newLocalNavigationState)
    }

    private fun getLocalNavigationState(state: NavigationState): NavigationState {
        val screen = state.chain.lastOrNull()
        if (screen is MultiScreen) {
            return getLocalNavigationState(screen.stacks[screen.selectedStack])
        } else {
            return state
        }
    }

    private fun applyNewLocalNavigationState(state: NavigationState, local: NavigationState): NavigationState {
        val screen = state.chain.lastOrNull()
        if (screen is MultiScreen) {
            val selectedStack = screen.stacks[screen.selectedStack]
            val newLocalChain = applyNewLocalNavigationState(selectedStack, local)

            if (newLocalChain.chain.isNotEmpty()) {
                val newStacks = screen.stacks.toMutableList()
                newStacks[screen.selectedStack] = newLocalChain
                val newMultiScreen = screen.copy(stacks = newStacks)

                val newChain = state.chain.dropLast(1).plus(newMultiScreen)
                return NavigationState(newChain)
            } else {
                val newChain = state.chain.dropLast(1)
                return NavigationState(newChain)
            }
        } else {
            return local
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
                val newMultiScreen = screen.copy(stacks = newStacks)

                val newChain = state.chain.dropLast(1).plus(newMultiScreen)
                return NavigationState(newChain)
            }
        } else {
            if (state.chain.none { it.id == action.screenId }) return null
            return origin(action, state)
        }
    }

    private fun applySelectStackAction(action: SelectStack, state: NavigationState): NavigationState {
        val screen = state.chain.lastOrNull() ?: return NavigationState()
        if (screen is MultiScreen) {
            val newMultiScreen = screen.copy(selectedStack = action.stackIndex)
            val newChain = state.chain.dropLast(1).plus(newMultiScreen)
            return NavigationState(newChain)
        } else {
            return state
        }
    }
}