package com.github.terrakok.modo.stack

import android.os.Parcelable
import com.github.terrakok.modo.NavModel
import com.github.terrakok.modo.NavigationAction
import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.Screen
import kotlinx.parcelize.Parcelize

typealias StackNavModel = NavModel<StackState>

fun StackNavModel(stack: List<Screen>) = StackNavModel(StackState(stack))
fun StackNavModel(screen: Screen) = StackNavModel(listOf(screen))

@Parcelize
data class StackState(
    val stack: List<Screen> = emptyList(),
) : NavigationState, Parcelable {

    constructor(vararg screensStack: Screen) : this(screensStack.toList())

    override fun getChildScreens(): List<Screen> = stack

}

class SetStack(val state: StackState) : NavigationAction
class Forward(val screen: Screen, vararg val screens: Screen) : NavigationAction
class Replace(val screen: Screen, vararg val screens: Screen) : NavigationAction
class NewStack(val screen: Screen, vararg val screens: Screen) : NavigationAction
class BackTo(val screen: Screen) : NavigationAction
class CompositeAction(vararg val actions: NavigationAction) : NavigationAction
class RemoveScreens(vararg val positions: Int) : NavigationAction
object BackToRoot : NavigationAction
object Back : NavigationAction
object Exit : NavigationAction

fun NavigationContainer<StackState>.setStack(state: StackState) = dispatch(SetStack(state))
fun NavigationContainer<StackState>.forward(screen: Screen, vararg screens: Screen) =
    dispatch(Forward(screen, *screens))

fun NavigationContainer<StackState>.replace(screen: Screen, vararg screens: Screen) = dispatch(Replace(screen, *screens))
fun NavigationContainer<StackState>.newStack(screen: Screen, vararg screens: Screen) = dispatch(NewStack(screen, *screens))
fun NavigationContainer<StackState>.backTo(screen: Screen) = dispatch(BackTo(screen))
fun NavigationContainer<StackState>.removeScreen(vararg positions: Int) = dispatch(RemoveScreens(*positions))
fun NavigationContainer<StackState>.backToRoot() = dispatch(BackToRoot)
fun NavigationContainer<StackState>.back() = dispatch(Back)
fun NavigationContainer<StackState>.exit() = dispatch(Exit)
fun NavigationContainer<StackState>.removeByPos(pos: Int) = dispatch(Exit)

class StackReducer : NavigationReducer<StackState> {
    override fun reduce(action: NavigationAction, state: StackState): StackState {
        return when (action) {
            is SetStack -> action.state
            is Forward -> StackState(
                state.stack + listOf(action.screen, *action.screens)
            )
            is Replace -> StackState(
                state.stack.dropLast(1) + listOf(action.screen, *action.screens)
            )
            is NewStack -> StackState(
                listOf(action.screen, *action.screens)
            )
            is BackTo -> {
                val i = state.stack.indexOfLast { it == action.screen }
                if (i != -1) StackState(state.stack.take(i + 1))
                else state
            }
            is BackToRoot -> StackState(
                listOfNotNull(state.stack.firstOrNull())
            )
            is Back -> StackState(
                state.stack.dropLast(1)
            )
            is RemoveScreens -> StackState(
                state.stack.filterIndexed { i, _ -> i !in action.positions }
            )
            is Exit -> StackState()
            else -> state
        }
    }
}