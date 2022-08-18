package com.github.terrakok.modo

data class StackNavigation(
    val stack: List<ScreenId> = emptyList()
): NavigationState

class SetStack(val state: StackNavigation) : NavigationAction
class Forward(val screen: Screen, vararg val screens: Screen) : NavigationAction
class Replace(val screen: Screen, vararg val screens: Screen) : NavigationAction
class NewStack(val screen: Screen, vararg val screens: Screen) : NavigationAction
class BackTo(val screenId: String) : NavigationAction
object BackToRoot : NavigationAction
object Back : NavigationAction
object Exit : NavigationAction

fun NavigationDispatcher.setStack(state: StackNavigation) = dispatch(SetStack(state))
fun NavigationDispatcher.forward(screen: Screen, vararg screens: Screen) = dispatch(Forward(screen, *screens))
fun NavigationDispatcher.replace(screen: Screen, vararg screens: Screen) = dispatch(Replace(screen, *screens))
fun NavigationDispatcher.newStack(screen: Screen, vararg screens: Screen) = dispatch(NewStack(screen, *screens))
fun NavigationDispatcher.backTo(screenId: String) = dispatch(BackTo(screenId))
fun NavigationDispatcher.backToRoot() = dispatch(BackToRoot)
fun NavigationDispatcher.back() = dispatch(Back)
fun NavigationDispatcher.exit() = dispatch(Exit)

class StackReducer : NavigationReducer {
    override fun reduce(action: NavigationAction, state: NavigationState): NavigationState? {
        if (state !is StackNavigation) return null
        return when (action) {
            is SetStack -> action.state
            is Forward -> StackNavigation(
                state.stack + listOf(action.screen, *action.screens)
            )
            is Replace -> StackNavigation(
                state.stack.dropLast(1) + listOf(action.screen, *action.screens)
            )
            is NewStack -> StackNavigation(
                listOf(action.screen, *action.screens)
            )
            is BackTo -> {
                val i = state.stack.indexOfLast { it.id == action.screenId }
                if (i != -1) StackNavigation(state.stack.take(i + 1))
                else state
            }
            is BackToRoot -> StackNavigation(
                listOfNotNull(state.stack.firstOrNull())
            )
            is Back -> StackNavigation(
                state.stack.dropLast(1)
            )
            is Exit -> StackNavigation()
            else -> null
        }
    }
}