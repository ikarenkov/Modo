package com.github.terrakok.modo.stack

import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.NavigationContainer
import com.github.terrakok.modo.ReducerAction
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.ScreenKey

@Deprecated("Use StackReducer instead.", ReplaceWith("StackReducer"))
fun interface StackReducerAction : ReducerAction<StackState>

fun interface StackReducer : NavigationReducer<StackState>

class SetStack(val state: StackState) : StackReducer {
    @Suppress("SpreadOperator")
    constructor(screen: Screen, vararg screens: Screen) : this(
        StackState(listOf(screen, *screens))
    )

    override fun reduce(oldState: StackState): StackState =
        state
}

class Forward(val screen: Screen, vararg val screens: Screen) : StackReducer {
    @Suppress("SpreadOperator")
    override fun reduce(oldState: StackState): StackState = StackState(
        oldState.stack + listOf(screen, *screens)
    )
}

class Replace(val screen: Screen, vararg val screens: Screen) : StackReducer {
    @Suppress("SpreadOperator")
    override fun reduce(oldState: StackState): StackState = if (oldState.stack.isNotEmpty()) {
        StackState(
            oldState.stack.dropLast(1) + listOf(screen, *screens)
        )
    } else {
        StackState(listOf(screen, *screens))
    }
}

/**
 * Backing to the first screen from the top of stack for which [backToCondition] is true.
 * @param including - if true, then the screen for which [backToCondition] is true will be also removed.
 */
class BackTo(
    val backToCondition: (pos: Int, screen: Screen) -> Boolean,
    val including: Boolean = false
) : StackReducer {

    constructor(screenKey: ScreenKey, including: Boolean = false) : this(
        { _, screen ->
            screen.screenKey == screenKey
        },
        including
    )

    constructor(screenBackTo: Screen, including: Boolean = false) : this(
        { _, screen ->
            screen == screenBackTo
        },
        including
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
        return if (foundPos != -1) {
            StackState(oldState.stack.take(foundPos + if (including) 0 else 1))
        } else {
            oldState
        }
    }

    companion object {
        inline operator fun <reified T : Screen> invoke(including: Boolean = false): StackReducer = BackTo(
            { _, screen -> screen is T },
            including
        )

        operator fun invoke(including: Boolean = false, condition: (pos: Int, screen: Screen) -> Boolean): StackReducer =
            BackTo(condition, including)
    }
}

class RemoveScreens(val condition: (pos: Int, screen: Screen) -> Boolean) : StackReducer {
    override fun reduce(oldState: StackState): StackState = StackState(
        oldState.stack.filterIndexed { i, screen -> !condition(i, screen) }
    )
}

/**
 * @param screensToDrop count of screens to drop from top of the stack
 * @param canEmptyStack if true, then stack can be empty after this action
 */
class Back(
    private val screensToDrop: Int = 1,
    private val canEmptyStack: Boolean = false
) : StackReducer {
    override fun reduce(oldState: StackState): StackState =
        if (canEmptyStack || oldState.stack.size > 1) {
            StackState(oldState.stack.dropLast(screensToDrop))
        } else {
            oldState
        }
}

fun StackNavContainer.dispatch(action: (StackState) -> StackState) = dispatch(NavigationReducer(action))

fun NavigationContainer<StackState>.forward(screen: Screen, vararg screens: Screen) = dispatch(Forward(screen, *screens))
fun NavigationContainer<StackState>.replace(screen: Screen, vararg screens: Screen) = dispatch(Replace(screen, *screens))
fun NavigationContainer<StackState>.setStack(screen: Screen, vararg screens: Screen) = dispatch(SetStack(screen, *screens))
fun NavigationContainer<StackState>.setState(state: StackState) = dispatch(SetStack(state))
fun NavigationContainer<StackState>.clearStack() = dispatch(SetStack(StackState()))

inline fun <reified T : Screen> NavigationContainer<StackState>.backTo(including: Boolean = false) = dispatch(BackTo<T>(including))
fun NavigationContainer<StackState>.backTo(screen: Screen, including: Boolean = false) = dispatch(BackTo(screen, including))
fun NavigationContainer<StackState>.backTo(screenKey: ScreenKey, including: Boolean = false) = dispatch(BackTo(screenKey, including))
fun NavigationContainer<StackState>.backTo(pos: Int, including: Boolean = false) = backTo(including) { backToPos, _ -> pos == backToPos }
fun NavigationContainer<StackState>.backTo(including: Boolean = false, backToCondition: (pos: Int, screen: Screen) -> Boolean) =
    dispatch(BackTo(including, backToCondition))

fun NavigationContainer<StackState>.backToRoot() = backTo(0)

fun NavigationContainer<StackState>.removeScreens(condition: (pos: Int, screen: Screen) -> Boolean) = dispatch(RemoveScreens(condition))

/**
 * @param screensToDrop count of screens to drop from top of the stack
 * @param canEmptyStack if true, then stack can be empty after this action
 */
fun NavigationContainer<StackState>.back(screensToDrop: Int = 1, canEmptyStack: Boolean = false) =
    dispatch(Back(screensToDrop, canEmptyStack))