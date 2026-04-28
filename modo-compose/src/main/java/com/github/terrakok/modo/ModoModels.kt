package com.github.terrakok.modo

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge

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
    val stateFlow: StateFlow<State>

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

/**
 * Observes navigation state changes across the entire subtree rooted at this container.
 *
 * Emits the current state on subscription, and re-emits whenever this container or *any* descendant
 * [NavigationContainer] dispatches. Observers are expected to re-walk via [NavigationState.getChildScreens]
 * to inspect the updated tree — emissions carry the root state, not nested states.
 *
 * Resubscription semantics: [flatMapLatest] cancels and rebuilds the inner subscription tree
 * whenever this container's own state changes, so subscriptions to removed children are
 * abandoned and newly added children are picked up automatically.
 *
 * To turn this into a hot [StateFlow], wrap the result with `stateIn(scope, started, initial)`
 * at the call site — the sharing policy is a consumer concern.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun NavigationContainer<*>.subtreeStateFlow(): Flow<NavigationState> =
    stateFlow.flatMapLatest { state ->
        flow {
            emit(state)
            // Children are re-read on every parent emission: when the parent dispatches to add
            // (or replace) children, flatMapLatest cancels this inner flow and a fresh one re-walks
            // getChildScreens(). So an "empty now, populated later" transition is handled by the
            // outer flatMapLatest, not by branching here.
            state.getChildScreens()
                .filterIsInstance<NavigationContainer<*>>()
                .map { it.subtreeStateFlow().drop(1) }
                .merge()
                .collect { emit(state) }
        }
    }

/**
 * Migration shim for the dev-branch `navigationStateFlow()` extension that produced a
 * `snapshotFlow { navigationState }` flow. The new API splits that into two operations,
 * so this shim makes silent migration impossible.
 *
 * - For per-container observation: use the [NavigationContainer.stateFlow] property.
 * - For whole-subtree observation (the previous behavior when consumers walked `getChildScreens()`):
 *   use [subtreeStateFlow].
 */
@Deprecated(
    message = "Replaced by the `stateFlow` property (per-container) and " +
        "`subtreeStateFlow()` (whole subtree). The previous snapshotFlow-based extension is gone.",
    replaceWith = ReplaceWith("subtreeStateFlow()"),
    level = DeprecationLevel.ERROR,
)
@Suppress("UNCHECKED_CAST", "unused")
fun <State : NavigationState> NavigationContainer<State>.navigationStateFlow(): Flow<State> =
    subtreeStateFlow() as Flow<State>
