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
@Deprecated("Use NavigationReducer directly.", ReplaceWith("NavigationReducer<State>"))
interface NavigationAction<State : NavigationState>

/**
 * Pure state transformer: takes old state and returns new state.
 */
fun interface NavigationReducer<State : NavigationState> {
    /**
     * Return a new state based on old [oldState].
     */
    fun reduce(oldState: State): State
}

/**
 * UDF navigation contract. State is exposed as a [StateFlow] and mutated exclusively through [dispatch].
 * The pure-Kotlin UDF implementation of this interface is [NavModel].
 * @param State - type of state that container manages.
 */
@Stable
interface NavigationContainer<State : NavigationState> {
    val navigationStateFlow: StateFlow<State>

    fun dispatch(reducer: NavigationReducer<State>)
}

/**
 * Extension to allow passing several reducers as a single atomic operation.
 * Reducers are applied in order, and the resulting state is dispatched once.
 *
 * This is particularly important for animations and other UI functionalities that depend on
 * a single state transition to avoid intermediate inconsistent states or multiple UI updates.
 */
fun <State : NavigationState> NavigationContainer<State>.dispatch(
    reducer: NavigationReducer<State>,
    vararg reducers: NavigationReducer<State>
) {
    dispatch { oldState ->
        var state = reducer.reduce(oldState)
        for (r in reducers) {
            state = r.reduce(state)
        }
        state
    }
}
