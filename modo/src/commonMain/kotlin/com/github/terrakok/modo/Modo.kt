package com.github.terrakok.modo

interface NavigationState {
    fun getChildScreens(): List<Screen>
}

interface NavigationAction

interface NavigationReducer<State : NavigationState> {
    fun reduce(action: NavigationAction, state: State): State
}

fun interface NavigationDispatcher {
    fun dispatch(action: NavigationAction)
}

interface NavigationRenderer {
    fun render(state: NavigationState)
}

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
//                        append(screen.id)
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
//            append(screen.id)
            appendLine()
            append(getNavigationStateString("$prefix|  ", screen.navigationState))
        }
        else -> "unknown state type: ${navigationState::class.simpleName}"
    }

internal expect fun logd(tag: String, msg: String)
