package com.github.terrakok.modo

data class StackNavigationState(
    val stack: List<Screen> = emptyList()
) : NavigationState {

    constructor(vararg screensStack: Screen) : this(screensStack.toList())

    override fun getChildScreens(): List<Screen> = stack

}

class SetStack(val state: StackNavigationState) : NavigationAction
class Forward(val screen: Screen, vararg val screens: Screen) : NavigationAction
class Replace(val screen: Screen, vararg val screens: Screen) : NavigationAction
class NewStack(val screen: Screen, vararg val screens: Screen) : NavigationAction
class BackTo(val screen: Screen) : NavigationAction
object BackToRoot : NavigationAction
object Back : NavigationAction
object Exit : NavigationAction

fun NavigationDispatcher.setStack(state: StackNavigationState) = dispatch(SetStack(state))
fun NavigationDispatcher.forward(screen: Screen, vararg screens: Screen) = dispatch(Forward(screen, *screens))
fun NavigationDispatcher.replace(screen: Screen, vararg screens: Screen) = dispatch(Replace(screen, *screens))
fun NavigationDispatcher.newStack(screen: Screen, vararg screens: Screen) = dispatch(NewStack(screen, *screens))
fun NavigationDispatcher.backTo(screen: Screen) = dispatch(BackTo(screen))
fun NavigationDispatcher.backToRoot() = dispatch(BackToRoot)
fun NavigationDispatcher.back() = dispatch(Back)
fun NavigationDispatcher.exit() = dispatch(Exit)

class StackReducer : NavigationReducer<StackNavigationState> {
    override fun reduce(action: NavigationAction, state: StackNavigationState): StackNavigationState {
        return when (action) {
            is SetStack -> action.state
            is Forward -> StackNavigationState(
                state.stack + listOf(action.screen, *action.screens)
            )
            is Replace -> StackNavigationState(
                state.stack.dropLast(1) + listOf(action.screen, *action.screens)
            )
            is NewStack -> StackNavigationState(
                listOf(action.screen, *action.screens)
            )
            is BackTo -> {
                val i = state.stack.indexOfLast { it == action.screen }
                if (i != -1) StackNavigationState(state.stack.take(i + 1))
                else state
            }
            is BackToRoot -> StackNavigationState(
                listOfNotNull(state.stack.firstOrNull())
            )
            is Back -> StackNavigationState(
                state.stack.dropLast(1)
            )
            is Exit -> StackNavigationState()
            else -> state
        }
    }
}