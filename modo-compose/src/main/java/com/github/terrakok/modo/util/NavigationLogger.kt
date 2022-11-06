package com.github.terrakok.modo.util

import android.util.Log
import com.github.terrakok.modo.MultiNavigation
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.containers.StackNavigationState
import com.github.terrakok.modo.containers.ContainerScreen


fun NavigationState?.print(): String =
    if (this == null) {
        "null"
    } else {
        getNavigationStateString("", this).trimEnd() + "  <- current screen"
    }

private fun getNavigationStateString(prefix: String, navigationState: NavigationState): String =
    when (navigationState) {
        is StackNavigationState -> {
            navigationState.stack.map { screen ->
                when (screen) {
                    is ContainerScreen<*> -> buildString {
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
        is MultiNavigation -> buildString {
            val screen = navigationState.containers[navigationState.selected]
            append(prefix)
            append(screen.screenKey)
            appendLine()
            append(getNavigationStateString("$prefix|  ", screen.navigationState))
        }
        else -> "unknown state type: ${navigationState::class.simpleName}"
    }

internal fun logd(tag: String, msg: String) {
    Log.d(tag, msg)
}