package com.github.terrakok.modo.util

import android.util.Log
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.multiscreen.MultiScreenState
import com.github.terrakok.modo.stack.StackState

fun NavigationState?.print(): String =
    if (this == null) {
        "null"
    } else {
        getNavigationStateString("", this).trimEnd() + "  <- current screen"
    }

private fun getNavigationStateString(prefix: String, navigationState: NavigationState): String =
    when (navigationState) {
        is StackState -> {
            navigationState.stack.map { screen ->
                when (screen) {
                    is ContainerScreen<*, *> -> buildString {
                        append(prefix)
                        append(screen.screenKey)
                        appendLine()
                        append(getNavigationStateString("$prefix|  ", screen.navigationState))
                    }
                    else -> {
                        "$prefix${screen}\n"
                    }
                }
            }.joinToString(separator = "")
        }
        is MultiScreenState -> buildString {
            val screen = navigationState.screens[navigationState.selected]
            append(prefix)
            append(screen.screenKey)
            appendLine()
            if (screen is ContainerScreen<*, *>) {
                append(getNavigationStateString("$prefix|  ", screen.navigationState))
            }
        }
        else -> "unknown state type: ${navigationState::class.simpleName}"
    }

internal fun logd(tag: String, msg: String) {
    Log.d(tag, msg)
}