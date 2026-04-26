package com.github.terrakok.modo

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.coroutines.flow.StateFlow

/**
 * State of navigation used in [NavigationContainer]. Can be any type.
 * You are obligated to return all [Screen]'s held by this state in [getChildScreens] for proper screens cleaning and lifecycle.
 * @see com.github.terrakok.modo.stack.StackState
 * @see com.github.terrakok.modo.multiscreen.MultiScreenState
 */
interface NavigationState : Parcelable {
    fun getChildScreens(): List<Screen>
}

/**
 * Marker interface to be able specify type of action for [NavigationContainer].
 */
interface NavigationAction<State>

fun interface NavigationReducer<State : NavigationState, Action : NavigationAction<State>> {
    /**
     * Return a new state based on old [state] and incoming action. If returns null, then the work will be addressed to parent reducer.
     */
    fun reduce(action: Action, state: State): State?
}

/**
 * UDF navigation contract. State is exposed as a [StateFlow] and mutated exclusively through [dispatch].
 * The pure-Kotlin UDF implementation of this interface is [NavModel].
 * @param State - type of state that container manages.
 * @param Action - type for actions that can be sent to [dispatch] to request state updates.
 */
@Stable
interface NavigationContainer<State : NavigationState, in Action : NavigationAction<State>> {
    val navigationStateFlow: StateFlow<State>

    fun dispatch(action: Action, vararg actions: Action)
}
