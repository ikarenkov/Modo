package com.github.terrakok.modo.stack

import com.github.terrakok.modo.NavigationAction
import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.ReducerAction
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey

interface StackAction : NavigationAction<StackState>

fun interface StackReducerAction : StackAction, ReducerAction<StackState>

class SetStack(val state: StackState) : StackReducerAction {
    @Suppress("SpreadOperator")
    constructor(screen: Screen, vararg screens: Screen) : this(
        StackState(listOf(screen, *screens))
    )

    override fun reduce(oldState: StackState): StackState =
        state
}

class Forward(val screen: Screen, vararg val screens: Screen) : StackReducerAction {
    @Suppress("SpreadOperator")
    override fun reduce(oldState: StackState): StackState = StackState(
        oldState.stack + listOf(screen, *screens)
    )
}

class Replace(val screen: Screen, vararg val screens: Screen) : StackReducerAction {
    @Suppress("SpreadOperator")
    override fun reduce(oldState: StackState): StackState = StackState(
        oldState.stack.dropLast(1) + listOf(screen, *screens)
    )
}

class BackTo(val backToCondition: (pos: Int, screen: Screen) -> Boolean) : StackReducerAction {

    constructor(screenKey: ScreenKey) : this(
        { _, screen ->
            screen.screenKey == screenKey
        }
    )

    constructor(screenBackTo: Screen) : this(
        { _, screen ->
            screen == screenBackTo
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

fun StackNavContainer.dispatch(action: (StackState) -> StackState) = dispatch(StackReducerAction(action))

fun NavigationContainer<StackState, StackAction>.forward(screen: Screen, vararg screens: Screen) = dispatch(Forward(screen, *screens))
fun NavigationContainer<StackState, StackAction>.replace(screen: Screen, vararg screens: Screen) = dispatch(Replace(screen, *screens))
fun NavigationContainer<StackState, StackAction>.setStack(screen: Screen, vararg screens: Screen) = dispatch(SetStack(screen, *screens))
fun NavigationContainer<StackState, StackAction>.setState(state: StackState) = dispatch(SetStack(state))
fun NavigationContainer<StackState, StackAction>.clearStack() = dispatch(SetStack(StackState()))

inline fun <reified T : Screen> NavigationContainer<StackState, StackAction>.backTo() = dispatch(BackTo<T>())
fun NavigationContainer<StackState, StackAction>.backTo(screen: Screen) = dispatch(BackTo(screen))
fun NavigationContainer<StackState, StackAction>.backTo(screenKey: ScreenKey) = dispatch(BackTo(screenKey))
fun NavigationContainer<StackState, StackAction>.backTo(pos: Int) = backTo { backToPos, _ -> pos == backToPos }
fun NavigationContainer<StackState, StackAction>.backTo(backToCondition: (pos: Int, screen: Screen) -> Boolean) = dispatch(BackTo(backToCondition))
fun NavigationContainer<StackState, StackAction>.backToRoot() = backTo(0)

fun NavigationContainer<StackState, StackAction>.removeScreens(condition: (pos: Int, screen: Screen) -> Boolean) = dispatch(RemoveScreens(condition))
fun NavigationContainer<StackState, StackAction>.back(screensToDrop: Int = 1) = dispatch(Back(screensToDrop))