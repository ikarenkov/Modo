package com.github.terrakok.modo.android

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.Lifecycle.State.CREATED
import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.lifecycle.Lifecycle.State.RESUMED
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.abs

/**
 * Manages screen lifecycle with coordination between parent lifecycle, composition state, and screen transitions.
 *
 * ## Core Contracts
 *
 * **1. Parent-Child Coordination**
 * - Child state never exceeds parent state (e.g., screen can't be [RESUMED] if parent is [STARTED])
 * - **Event propagation from parent:**
 *   - [ON_CREATE]: Never propagated (subscription happens after screen creation)
 *   - [ON_START]: Always propagated (parent going up brings child up)
 *   - [ON_RESUME]: Propagated but gated by [canResumeAfterTransition] (requires visible screen with completed transitions)
 *   - [ON_PAUSE]/[ON_STOP]: Always propagated (parent going down forces child down immediately)
 *   - [ON_DESTROY]: Conditionally propagated (blocked during config changes to preserve SavedStateRegistry)
 *
 * **2. Transition Readiness ([canResumeAfterTransition])**
 * - [ON_RESUME] blocked until show transition completes via [showTransitionFinished]
 * - Hide transition via [hideTransitionStarted] triggers immediate pause
 * - Prevents screen from being [RESUMED] during hide animations
 *
 * **3. Sequential Progression**
 * - Lifecycle events must be called in sequential order ([CREATED] -> [STARTED] -> [RESUMED])
 * - Single-step transitions enforced (except [CREATED] -> [DESTROYED])
 * - Redundant events skipped automatically (e.g., [ON_START] when already [RESUMED])
 *
 * **4. No Resurrection**
 * - Once [DESTROYED], no further lifecycle events accepted
 * - Ensures proper cleanup and prevents use-after-destroy bugs
 *
 * ## Usage
 *
 * Typical lifecycle flow:
 * 1. Screen created: `updateLifecycleIfNeeded(`[ON_CREATE]`)`
 * 2. Enters composition: `handleCompositionEnter(manualResumePause)`
 * 3. Subscribe to parent: `subscribeToParentLifecycle(...)`
 * 4. Transition complete: `showTransitionFinished()` -> moves to [RESUMED]
 * 5. Exits composition: `handleCompositionExit(manualResumePause)`
 * 6. Screen destroyed: `updateLifecycleIfNeeded(`[ON_DESTROY]`)`
 */
internal class ScreenLifecycleManager(
    lifecycleOwner: LifecycleOwner
) {
    val lifecycle: LifecycleRegistry = LifecycleRegistry(lifecycleOwner)

    internal val parentLifecycleOwner = AtomicReference<LifecycleOwner>()

    /**
     * `true` - when related screen is not animating and visible
     * `false` - when related screen animating or idle but after hide transition
     */
    @Volatile
    private var canResumeAfterTransition: Boolean = false

    private val currentParentState: Lifecycle.State?
        get() = parentLifecycleOwner.get()?.lifecycle?.currentState

    fun handleCompositionEnter(manualResumePause: Boolean) {
        updateLifecycleIfNeeded(ON_START)
        if (!manualResumePause) {
            canResumeAfterTransition = true
            updateLifecycleIfNeeded(ON_RESUME)
        }
    }

    fun handleCompositionExit() {
        // sending pause anyway, it will be ignored if it is already handled
        updateLifecycleIfNeeded(ON_PAUSE)
        updateLifecycleIfNeeded(ON_STOP)
    }

    fun showTransitionFinished() {
        canResumeAfterTransition = true
        updateLifecycleIfNeeded(ON_RESUME)
    }

    fun hideTransitionStarted() {
        canResumeAfterTransition = false
        updateLifecycleIfNeeded(ON_PAUSE)
    }

    /**
     * @param onEventBeforePropagation Callback for side-effects (e.g., SavedState persistence) before lifecycle propagation
     * @return Unregister callback
     */
    fun subscribeToParentLifecycle(
        parentLifecycleOwner: LifecycleOwner,
        isActivityFinishing: () -> Boolean? = { null },
        isChangingConfigurations: () -> Boolean? = { null },
        onEventBeforePropagation: ((Lifecycle.Event) -> Unit)? = null
    ): () -> Unit {
        val observer = LifecycleEventObserver { _, event ->
            // Allow adapter to handle side-effects (like SavedState)
            onEventBeforePropagation?.invoke(event)

            // Propagate lifecycle events using manager's logic
            // [ON_DESTROY] blocked during config changes to preserve SavedStateRegistry.
            if (event != ON_DESTROY || (isActivityFinishing() != false && isChangingConfigurations() != true)) {
                updateLifecycleIfNeeded(event)
            }
        }

        parentLifecycleOwner.lifecycle.addObserver(observer)
        return {
            parentLifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    /**
     * Enforces lifecycle rules:
     * - No resurrection after [DESTROYED]
     * - [ON_RESUME] blocked until [canResumeAfterTransition]
     * - Child state never exceeds parent state
     * - Single-step transitions only (except [CREATED] -> [DESTROYED])
     */
    fun updateLifecycleIfNeeded(event: Lifecycle.Event) {
        @Suppress("ComplexCondition")
        if (
            lifecycle.currentState != DESTROYED &&
            !stateAlreadyReached(lifecycle.currentState, event) &&
            (event != ON_RESUME || canResumeAfterTransition) &&
            parentStateAllowsTransition(currentParentState, event)
        ) {
            assert(
                abs(lifecycle.currentState.ordinal - event.targetState.ordinal) == 1 ||
                    lifecycle.currentState == CREATED && event == ON_DESTROY
            ) {
                "Lifecycle state transition must be one step, but was ${lifecycle.currentState} -> $event"
            }
            lifecycle.handleLifecycleEvent(event)
        }
    }

    companion object {
        private val MOVE_LIFECYCLE_STATE_UP_EVENTS = setOf(
            ON_CREATE,
            ON_START,
            ON_RESUME
        )

        private val MOVE_LIFECYCLE_STATE_DOWN_EVENTS = setOf(
            ON_STOP,
            ON_PAUSE,
            ON_DESTROY
        )

        /**
         * Skips redundant events (e.g., [ON_START] when already [RESUMED], or [ON_PAUSE] when already [CREATED]).
         */
        internal fun stateAlreadyReached(currentState: Lifecycle.State, event: Lifecycle.Event): Boolean =
            // Skipping events that move the lifecycle state up, but this state is already reached.
            (event in MOVE_LIFECYCLE_STATE_UP_EVENTS && event.targetState <= currentState) ||
                // Skipping events that move the lifecycle state down, but this state is already reached.
                (event in MOVE_LIFECYCLE_STATE_DOWN_EVENTS && event.targetState >= currentState)

        private fun parentStateAllowsTransition(
            parentState: Lifecycle.State?,
            event: Lifecycle.Event,
        ): Boolean =
            event in MOVE_LIFECYCLE_STATE_DOWN_EVENTS ||
                event.targetState <= CREATED ||
                parentState != null && parentState >= event.targetState
    }
}
