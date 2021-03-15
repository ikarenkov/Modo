package com.github.terrakok.modo.android.compose

import android.util.Log
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
        Log.d("Modo", "New state=$newState")
        return newState
    }
}