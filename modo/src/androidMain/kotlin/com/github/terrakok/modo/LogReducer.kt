package com.github.terrakok.modo

import android.util.Log

/**
 * Additional reducer for logging changes of navigation state.
 */
class LogReducer(private val origin: NavigationReducer) : NavigationReducer {
    override fun invoke(action: NavigationAction, state: NavigationState): NavigationState {
        Log.d("Modo", "New action=$action")
        val newState = origin.invoke(action, state)
        Log.d("Modo", "New state=$newState")
        return newState
    }
}