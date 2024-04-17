package com.github.terrakok.modo.stack

import com.github.terrakok.modo.NavigationAction
import com.github.terrakok.modo.ReducerAction
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey

interface StackAction : NavigationAction<StackState>

fun interface StackReducerAction : StackAction, ReducerAction<StackState>

class SetStack(val state: StackState) : StackReducerAction {
    override fun reduce(oldState: StackState): StackState =
        state
}

class Forward(val screen: Screen, vararg val screens: Screen) : StackReducerAction {
    override fun reduce(oldState: StackState): StackState = StackState(
        oldState.stack + listOf(screen, *screens)
    )
}

class Replace(val screen: Screen, vararg val screens: Screen) : StackReducerAction {
    override fun reduce(oldState: StackState): StackState = StackState(
        oldState.stack.dropLast(1) + listOf(screen, *screens)
    )
}

class NewStack(val screen: Screen, vararg val screens: Screen) : StackReducerAction {
    override fun reduce(oldState: StackState): StackState = StackState(
        listOf(screen, *screens)
    )
}

class BackTo(val backToCondition: (pos: Int, screen: Screen) -> Boolean) : StackReducerAction {
    override fun reduce(oldState: StackState): StackState {
        val stack = oldState.stack
        var foundPos = -1
        for (i in stack.lastIndex downTo 0) {
            if (backToCondition(i, stack[i])) {
                foundPos = i
                break
            }
        }
        return if (foundPos != -1) StackState(oldState.stack.take(foundPos + 1)) else oldState
    }

    companion object {
        operator fun invoke(screenKey: ScreenKey): StackReducerAction = BackTo { _, screen ->
            screen.screenKey == screenKey
        }

        operator fun invoke(backToScreen: Screen): StackReducerAction = BackTo { _, screen ->
            screen == backToScreen
        }
    }
}

class RemoveScreens(val condition: (pos: Int, screen: Screen) -> Boolean) : StackReducerAction {
    override fun reduce(oldState: StackState): StackState = StackState(
        oldState.stack.filterIndexed { i, screen -> !condition(i, screen) }
    )
}

object BackToRoot : StackReducerAction {
    override fun reduce(oldState: StackState): StackState = StackState(
        listOfNotNull(oldState.stack.firstOrNull())
    )
}

object Back : StackReducerAction {
    override fun reduce(oldState: StackState): StackState = StackState(
        oldState.stack.dropLast(1)
    )
}

fun StackContainer.setStack(state: StackState) = dispatch(SetStack(state))
fun StackContainer.forward(screen: Screen, vararg screens: Screen) =
    dispatch(Forward(screen, *screens))

fun StackContainer.replace(screen: Screen, vararg screens: Screen) = dispatch(Replace(screen, *screens))
fun StackContainer.newStack(screen: Screen, vararg screens: Screen) = dispatch(NewStack(screen, *screens))
fun StackContainer.backTo(screen: Screen) = dispatch(BackTo(screen))
fun StackContainer.backTo(screenKey: ScreenKey) = dispatch(BackTo(screenKey))
fun StackContainer.backTo(backToCondition: (pos: Int, screen: Screen) -> Boolean) = dispatch(BackTo(backToCondition))
fun StackContainer.removeScreens(condition: (pos: Int, screen: Screen) -> Boolean) = dispatch(RemoveScreens(condition))
fun StackContainer.backToRoot() = dispatch(BackToRoot)
fun StackContainer.back() = dispatch(Back)