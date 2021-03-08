package com.github.terrakok.modo

data class MultiScreen(
    override val id: String,
    val stacks: List<NavigationState>,
    val selectedStack: Int
) : Screen

class ExternalForward(val screen: Screen, vararg val screens: Screen) : NavigationAction
object BackToTabRoot : NavigationAction
data class SelectStack(val stackIndex: Int) : NavigationAction

fun Modo.externalForward(screen: Screen, vararg screens: Screen) =
    dispatch(ExternalForward(screen, *screens))

fun Modo.selectStack(stackIndex: Int) = dispatch(SelectStack(stackIndex))
fun Modo.backToTabRoot() = dispatch(BackToTabRoot)

class MultiReducer(
    private val origin: ModoReducer = ModoReducer()
) : NavigationReducer {
    override fun invoke(action: NavigationAction, state: NavigationState): NavigationState {
        val currentScreen = state.chain.lastOrNull()

        return if (currentScreen is MultiScreen) {
            when {
                action is ExternalForward -> {
                    origin(Forward(action.screen, *action.screens), state)
                }
                action is BackToTabRoot -> {
                    val localState = currentScreen.stacks[currentScreen.selectedStack]
                    val newLocalState =
                        NavigationState(listOfNotNull(localState.chain.firstOrNull()))

                    val newStacks = currentScreen.stacks.toMutableList()
                    newStacks[currentScreen.selectedStack] = newLocalState

                    val newScreen = currentScreen.copy(stacks = newStacks)
                    origin(Replace(newScreen), state)
                }
                action is SelectStack -> {
                    val newScreen = currentScreen.copy(selectedStack = action.stackIndex)
                    origin(Replace(newScreen), state)
                }
                standardActionIsApplicable(currentScreen, action) -> {
                    val newScreen = applyOriginToMultiScreen(action, currentScreen)
                    origin(Replace(newScreen), state)
                }
                else -> {
                    origin(action, state)
                }
            }
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