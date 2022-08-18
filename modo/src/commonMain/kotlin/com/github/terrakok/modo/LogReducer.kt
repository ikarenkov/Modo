package com.github.terrakok.modo

/**
 * Additional reducer for logging changes of navigation state.
 */
class LogReducer(
    private val origin: NavigationReducer,
    private val onNewAction: (NavigationAction) -> Unit = { action ->
        logd("Modo", "New action=$action")
    },
    private val onNewState: (NavigationState?) -> Unit = { navigationState ->
        logd("Modo", "New state=${navigationState.hashCode()}\n${navigationState.format()}")
    }
) : NavigationReducer {
    override fun reduce(action: NavigationAction, state: NavigationState): NavigationState? {
        onNewAction(action)
        return origin.reduce(action, state).also(onNewState)
    }
}

fun NavigationState?.format(): String =
    if (this == null) {
        "null"
    } else {
        getNavigationStateString("", this).trimEnd() + "  <- current screen"
    }

private fun getNavigationStateString(prefix: String, navigationState: NavigationState): String =
    when (navigationState) {
        is StackNavigation -> {
            navigationState.stack.map { screen ->
                when (screen) {
                    is ContainerScreen -> buildString {
                        append(prefix)
                        append(screen.id)
                        appendLine()
                        append(getNavigationStateString("$prefix|  ", screen.navigationState))
                    }
                    else -> {
                        "$prefix${screen.id}\n"
                    }
                }
            }.joinToString(separator = "")
        }
        is MultiNavigation -> buildString {
            val screen = navigationState.containers[navigationState.activeContainerIndex]
            append(prefix)
            append(screen.id)
            appendLine()
            append(getNavigationStateString("$prefix|  ", screen.navigationState))
        }
        else -> "unknown state type: ${navigationState::class.simpleName}"
    }

internal expect fun logd(tag: String, msg: String)