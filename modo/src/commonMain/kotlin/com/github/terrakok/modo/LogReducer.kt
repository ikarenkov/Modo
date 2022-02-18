package com.github.terrakok.modo

/**
 * Additional reducer for logging changes of navigation state.
 */
class LogReducer(
    private val origin: NavigationReducer,
    private val onNewAction: (NavigationAction) -> Unit = { action ->
        logd("Modo", "New action=$action")
    },
    private val onNewState: (NavigationState) -> Unit = { navigationState ->
        logd("Modo", "New state=${navigationState.hashCode()}\n${navigationState.format()}")
    }
) : NavigationReducer {
    override fun invoke(action: NavigationAction, state: NavigationState): NavigationState {
        onNewAction(action)
        return origin.invoke(action, state).also(onNewState)
    }
}

fun NavigationState.format(): String =
    "‣root\n${getNavigationStateString("⦙  ", this).trimEnd()}  ᐊ current screen"

fun getNavigationStateString(prefix: String, navigationState: NavigationState): String =
    navigationState.chain.map { screen ->
        when (screen) {
            is AbstractMultiScreen -> buildString {
                append(prefix)
                append('‣')
                append(screen.id)
                if (screen.stacks.size > 1) {
                    append(" [${screen.selectedStack + 1}/${screen.stacks.size}]")
                }
                append('\n')
                append(getNavigationStateString("$prefix⦙  ", screen.stacks[screen.selectedStack]))
            }
            else -> {
                "$prefix${screen.id}\n"
            }
        }
    }.joinToString(separator = "")

expect fun logd(tag: String, msg: String)