package com.github.terrakok.modo

import android.os.Parcelable
import androidx.compose.runtime.Stable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.shareIn

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

    /**
     * Atomically applies [reducer] to the current state.
     *
     * Reducers MUST be pure functions of their input state: on contended dispatches the
     * implementation may invoke the reducer multiple times (compare-and-set retry) before
     * one application wins and is published. Any side effect performed inside the reducer
     * will therefore execute an unspecified number of times — perform side effects outside
     * the reducer (e.g. before/after calling [dispatch]).
     */
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
 * Cold [Flow] that observes navigation state changes across the entire subtree rooted at this container.
 *
 * Emits the current state on collection, and re-emits whenever this container or *any* descendant
 * [NavigationContainer] dispatches. Observers are expected to re-walk via [NavigationState.getChildScreens]
 * to inspect the updated tree — emissions carry the root state, not nested states.
 *
 * Because this is a cold flow, no [CoroutineScope] is needed at the call site. Use [subtreeStateFlow]
 * for a hot [StateFlow] with a synchronously accessible current value.
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun NavigationContainer<*>.subtreeFlow(): Flow<NavigationState> =
    stateFlow.flatMapLatest { state ->
        flow {
            emit(state)
            // Children are re-read on every parent emission: when the parent dispatches to add
            // (or replace) children, flatMapLatest cancels this inner flow and a fresh one re-walks
            // getChildScreens(). So an "empty now, populated later" transition is handled by the
            // outer flatMapLatest, not by branching here.
            state.getChildScreens()
                .filterIsInstance<NavigationContainer<*>>()
                .map { it.subtreeFlow().drop(1) }
                .merge()
                .collect { emit(state) }
        }
    }

/**
 * Hot [StateFlow] that observes navigation state changes across the entire subtree rooted at this container.
 *
 * Emits the current root state on collection, and re-emits whenever this container or *any* descendant
 * [NavigationContainer] dispatches. Observers are expected to re-walk via [NavigationState.getChildScreens]
 * to inspect the updated tree — emissions carry the root state, not nested states.
 *
 * Note: unlike a typical [StateFlow], same-value re-emissions are NOT deduplicated — a nested dispatch
 * does not change the root state object but must still trigger observers.
 *
 * @param scope the [CoroutineScope] that keeps the returned [StateFlow] active.
 */
fun NavigationContainer<*>.subtreeStateFlow(scope: CoroutineScope): StateFlow<NavigationState> {
    // SharedFlow(replay=1) preserves all emissions without equals-based deduplication,
    // which is required because nested dispatches re-emit the unchanged root state as a signal.
    // Started eagerly so `.value` / `.collect` always observe at least the initial root state without
    // a first-subscriber timing window.
    val shared: SharedFlow<NavigationState> =
        subtreeFlow().shareIn(scope, SharingStarted.Eagerly, replay = 1)
    return object : StateFlow<NavigationState> {
        override val value: NavigationState
            get() = shared.replayCache.firstOrNull() ?: stateFlow.value
        override val replayCache: List<NavigationState>
            get() = shared.replayCache.ifEmpty { listOf(stateFlow.value) }
        override suspend fun collect(collector: FlowCollector<NavigationState>): Nothing =
            shared.collect(collector)
    }
}

/**
 * Migration shim for the previous `navigationState` property on [NavigationContainer]. Pick the
 * replacement that matches the consumer's actual intent:
 *
 * - [NavigationContainer.stateFlow].value — one-shot read of this container's state.
 * - `stateFlow.collectAsState()` — Compose-reactive read of this container's state.
 * - [subtreeStateFlow] — hot StateFlow observing this container AND every nested container.
 * - [subtreeFlow] — cold Flow equivalent of [subtreeStateFlow], no scope required.
 * - Or change the declared type to the concrete ContainerScreen subtype (StackScreen,
 *   MultiScreen, ...) which still exposes Compose-reactive `navigationState`.
 */
@Deprecated(
    message = "navigationState was removed from NavigationContainer. Pick a migration:\n" +
        " - stateFlow.value             — one-shot read of this container's state\n" +
        " - stateFlow.collectAsState()  — Compose-reactive read of this container's state\n" +
        " - subtreeStateFlow(scope)     — hot StateFlow observing this container AND every nested container (whole subtree)\n" +
        " - subtreeFlow()               — cold Flow equivalent of subtreeStateFlow, no scope required\n" +
        " - or change the declared type to the concrete ContainerScreen subtype (StackScreen, MultiScreen, ...) " +
        "which still exposes Compose-reactive navigationState.",
    replaceWith = ReplaceWith("stateFlow.value"),
    level = DeprecationLevel.ERROR,
)
@Suppress("unused")
val <State : NavigationState> NavigationContainer<State>.navigationState: State
    get() = stateFlow.value

/**
 * Migration shim for the dev-branch `navigationStateFlow()` extension that produced a
 * `snapshotFlow { navigationState }` flow. The closest replacement is the per-container
 * [NavigationContainer.stateFlow] property; for whole-subtree observation use [subtreeFlow]
 * (cold) or [subtreeStateFlow] (hot).
 */
@Deprecated(
    message = "Replaced by the `stateFlow` property (per-container) and " +
        "`subtreeFlow( сс                        ии  )` / `subtreeStateFlow(scope)` (whole subtree). " +
        "The previous snapshotFlow-based extension is gone.",
    replaceWith = ReplaceWith("stateFlow"),
    level = DeprecationLevel.ERROR,
)
@Suppress("unused")
fun <State : NavigationState> NavigationContainer<State>.navigationStateFlow(): Flow<State> =
    stateFlow
