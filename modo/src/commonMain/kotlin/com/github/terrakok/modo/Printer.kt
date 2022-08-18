package com.github.terrakok.modo

fun NavigationState?.print(): String =
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