package com.github.terrakok.modo.animation

import com.github.terrakok.modo.ComposeRendererScope
import com.github.terrakok.modo.stack.StackState

enum class StackTransitionType {
    Push,
    Replace,
    Pop,
    Idle
}

fun calculateStackTransitionType(oldScreensState: StackState?, newScreensState: StackState?): StackTransitionType =
    if (oldScreensState != null && newScreensState != null) {
        val oldStack = oldScreensState.stack
        val newStack = newScreensState.stack
        when {
            oldStack.lastOrNull() == newStack.lastOrNull() || oldStack.isEmpty() -> StackTransitionType.Idle
            newStack.lastOrNull() in oldStack -> StackTransitionType.Pop
            oldStack.lastOrNull() in newStack -> StackTransitionType.Push
            else -> StackTransitionType.Replace
        }
    } else {
        StackTransitionType.Idle
    }

fun ComposeRendererScope<StackState>.calculateStackTransitionType() = calculateStackTransitionType(oldState, newState)