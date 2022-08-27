package com.github.terrakok.modo

data class StackNavigation(
    val stack: List<Screen> = emptyList()
): NavigationState {
    override fun getAllScreens(): List<Screen> = stack
    override fun getActiveScreen(): Screen? = stack.lastOrNull()
}

class SetStack(val state: StackNavigation) : NavigationAction
class Forward(val screen: Screen, vararg val screens: Screen) : NavigationAction
class Replace(val screen: Screen, vararg val screens: Screen) : NavigationAction
class NewStack(val screen: Screen, vararg val screens: Screen) : NavigationAction
class BackTo(val screenId: String) : NavigationAction
object BackToRoot : NavigationAction
object Back : NavigationAction
object Exit : NavigationAction

fun Navigator.setStack(state: StackNavigation) = dispatch(SetStack(state))
fun Navigator.forward(screen: Screen, vararg screens: Screen) = dispatch(Forward(screen, *screens))
fun Navigator.replace(screen: Screen, vararg screens: Screen) = dispatch(Replace(screen, *screens))
fun Navigator.newStack(screen: Screen, vararg screens: Screen) = dispatch(NewStack(screen, *screens))
fun Navigator.backTo(screenId: String) = dispatch(BackTo(screenId))
fun Navigator.backToRoot() = dispatch(BackToRoot)
fun Navigator.back() = dispatch(Back)
fun Navigator.exit() = dispatch(Exit)

class StackReducer : NavigationReducer<StackNavigation> {
    override fun reduce(action: NavigationAction, state: StackNavigation): StackNavigation? {
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