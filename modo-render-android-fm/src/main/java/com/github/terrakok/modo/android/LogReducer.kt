package com.github.terrakok.modo.android

import android.util.Log
import com.github.terrakok.modo.MultiScreen
import com.github.terrakok.modo.NavigationAction
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.NavigationState

/**
 * Additional reducer for logging changes of navigation state.
 */
class LogReducer(private val origin: NavigationReducer) : NavigationReducer {
    override fun invoke(action: NavigationAction, state: NavigationState): NavigationState {
        Log.d("Modo", "New action=$action")
        val newState = origin.invoke(action, state)
        val stateStr = "‣root\n${getNavigationStateString("⦙  ", newState).trimEnd()}  ᐊ current screen"
        Log.d("Modo", "New state=${newState.hashCode()}\n$stateStr")
        return newState
    }

    private fun getNavigationStateString(prefix: String, navigationState: NavigationState): String =
        navigationState.chain.map { screen ->
            when (screen) {
                is MultiScreen -> buildString {
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
}