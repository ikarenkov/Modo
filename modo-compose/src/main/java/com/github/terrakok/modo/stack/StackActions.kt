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

    constructor(screenKey: ScreenKey) : this(
        { _, screen ->
            screen.screenKey == screenKey
        }
    )

    constructor(screen: Screen) : this(
        { _, screen ->
            screen == screen
        }
    )

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
        inline operator fun <reified T : Screen> invoke(): StackReducerAction = BackTo { _, screen ->
            screen is T
        }
    }
}

class RemoveScreens(val condition: (pos: Int, screen: Screen) -> Boolean) : StackReducerAction {
    override fun reduce(oldState: StackState): StackState = StackState(
        oldState.stack.filterIndexed { i, screen -> !condition(i, screen) }
    )
}

class Back(private val screensToDrop: Int = 1) : StackReducerAction {
    override fun reduce(oldState: StackState): StackState = StackState(
        oldState.stack.dropLast(screensToDrop)
    )
}

fun StackNavigationContainer.dispatch(action: (StackState) -> StackState) = dispatch(StackReducerAction(action))

fun StackContainer.setStack(state: StackState) = dispatch(SetStack(state))
fun StackContainer.forward(screen: Screen, vararg screens: Screen) = dispatch(Forward(screen, *screens))
fun StackContainer.replace(screen: Screen, vararg screens: Screen) = dispatch(Replace(screen, *screens))
fun StackContainer.newStack(screen: Screen, vararg screens: Screen) = dispatch(NewStack(screen, *screens))

inline fun <reified T : Screen> StackContainer.backTo() = dispatch(BackTo<T>())
fun StackContainer.backTo(screen: Screen) = dispatch(BackTo(screen))
fun StackContainer.backTo(screenKey: ScreenKey) = dispatch(BackTo(screenKey))
fun StackContainer.backTo(backToCondition: (pos: Int, screen: Screen) -> Boolean) = dispatch(BackTo(backToCondition))

fun StackContainer.removeScreens(condition: (pos: Int, screen: Screen) -> Boolean) = dispatch(RemoveScreens(condition))
fun StackContainer.back(screensToDrop: Int = 1) = dispatch(Back(screensToDrop))