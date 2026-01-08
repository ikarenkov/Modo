package com.github.terrakok.modo.animation

import com.github.terrakok.modo.ComposeRendererScope
import com.github.terrakok.modo.ExperimentalModoApi
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.stack.StackState

/**
 * Describes the transition types for a stack.
 */
enum class StackTransitionType {
    /**
     * Represents opening a new screen in a stack.
     */
    Push,

    /**
     * Represents replacement of current screen in a stack to a new one.
     */
    Replace,

    /**
     * Represents closing the current screen in a stack.
     */
    Pop,

    /**
     * Represents no transition.
     */
    Idle
}

/**
 * Culculates the transition type for the given [oldState] and [newState].
 * It can be used with a combination with [ScreenTransitionContent] to culculate transitionSpec based on [StackTransitionType].
 */
@OptIn(ExperimentalModoApi::class)
fun ComposeRendererScope<StackState>.calculateStackTransitionType(): StackTransitionType =
    if (oldState != null && newState != null) {
        val oldStack = oldState.stack
        val newStack = newState.stack
        when {
            oldStack.lastOrNull() == newStack.lastOrNull() || oldStack.isEmpty() -> StackTransitionType.Idle
            newStack.lastOrNull() in oldStack -> StackTransitionType.Pop
            oldStack.lastOrNull() in newStack -> StackTransitionType.Push
            else -> StackTransitionType.Replace
        }
    } else {
        StackTransitionType.Idle
    }

fun calculateStackTransitionType(
    oldStack: List<Screen>,
    newStack: List<Screen>,
    firstScreenIdle: Boolean = true
): StackTransitionType {
    return when {
        firstScreenIdle && oldStack.isEmpty() || oldStack.lastOrNull() == newStack.lastOrNull() -> StackTransitionType.Idle
        newStack.isEmpty() || newStack.lastOrNull() in oldStack -> StackTransitionType.Pop
        oldStack.isEmpty() || oldStack.lastOrNull() in newStack -> StackTransitionType.Push
        else -> StackTransitionType.Replace
    }
}