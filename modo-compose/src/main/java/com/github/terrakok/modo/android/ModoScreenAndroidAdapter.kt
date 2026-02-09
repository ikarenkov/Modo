package com.github.terrakok.modo.android

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.Lifecycle.State.CREATED
import androidx.lifecycle.Lifecycle.State.DESTROYED
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.SAVED_STATE_REGISTRY_OWNER_KEY
import androidx.lifecycle.SavedStateViewModelFactory
import androidx.lifecycle.VIEW_MODEL_STORE_OWNER_KEY
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.enableSavedStateHandles
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.compose.LocalSavedStateRegistryOwner
import com.github.terrakok.modo.ModoDevOptions
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.SetupPreDispose
import com.github.terrakok.modo.android.ModoScreenAndroidAdapter.Companion.needPropagateLifecycleEventFromParent
import com.github.terrakok.modo.lifecycle.LifecycleDependency
import com.github.terrakok.modo.logs.devLogD
import com.github.terrakok.modo.logs.devLogI
import com.github.terrakok.modo.logs.devLogV
import com.github.terrakok.modo.model.ScreenModelStore
import com.github.terrakok.modo.model.ScreenModelStore.remove
import com.github.terrakok.modo.util.getActivity
import com.github.terrakok.modo.util.getApplication
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.abs

/**
 * Adapter for Screen that provides android-related features support using Modo, such as:
 * 1. ViewModel
 * 2. Lifecycle
 * 3. SavedState
 *
 * It the single instance of [ModoScreenAndroidAdapter] per Screen.
 */
class ModoScreenAndroidAdapter private constructor(
    // For debugging purposes
    internal val screen: Screen
) :
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner,
    HasDefaultViewModelProviderFactory,
    LifecycleDependency {

    override val lifecycle: LifecycleRegistry = LifecycleRegistry(this)

    override val viewModelStore: ViewModelStore = ViewModelStore()

    override val savedStateRegistry: SavedStateRegistry
        get() = controller.savedStateRegistry

    override val defaultViewModelProviderFactory: ViewModelProvider.Factory
        get() = SavedStateViewModelFactory(
            application = atomicContext.get()?.applicationContext?.getApplication(),
            owner = this
        )

    override val defaultViewModelCreationExtras: CreationExtras
        get() = MutableCreationExtras().apply {
            application?.let {
                set(ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY, it)
            }
            set(SAVED_STATE_REGISTRY_OWNER_KEY, this@ModoScreenAndroidAdapter)
            set(VIEW_MODEL_STORE_OWNER_KEY, this@ModoScreenAndroidAdapter)

            /* TODO if (getArguments() != null) {
                extras.set<Bundle>(DEFAULT_ARGS_KEY, getArguments())
            }*/
        }
    private val controller = SavedStateRegistryController.create(this)
    private var isCreated: Boolean by mutableStateOf(false)

    // Atomic references for cases when we unable take it directly from the composition.
    private val atomicContext = AtomicReference<Context>()

    @VisibleForTesting
    internal val atomicParentLifecycleOwner = AtomicReference<LifecycleOwner>()
    private val application: Application? get() = atomicContext.get()?.applicationContext?.getApplication()

    /**
     * Holding transition state of the screen to be able to handle lifecycle events from parent properly.
     * Check out [needPropagateLifecycleEventFromParent] for more details.
     */
    private val screenTransitionState: ScreenTransitionState = ScreenTransitionState(readyToBeResumed = false)

    init {
        controller.performAttach()
        enableSavedStateHandles()
    }

    @Composable
    fun ProvideAndroidIntegration(
        manualResumePause: Boolean = false,
        content: @Composable () -> Unit,
    ) {
        val context: Context = LocalContext.current
        val parentLifecycleOwner = LocalLifecycleOwner.current
        DisposableAtomicReference(LocalContext, atomicContext)
        DisposableAtomicReference(LocalLifecycleOwner, atomicParentLifecycleOwner)
        LifecycleDisposableEffect(context, parentLifecycleOwner, manualResumePause) {
            ProvideCompositionLocals(content)
        }
    }

    /**
     * Must be called before [remove] to inform that this screen is going to be removed.
     * We need it to provide support of using DisposableEffect or/and LaunchedEffect inside [Screen.Content].
     * F.e. to be able to collect ON_DISPOSE lifecycle event.
     */
    override fun onPreDispose() {
        ModoDevOptions.onScreenPreDisposeListener?.invoke(screen)
        updateLifecycleIfNeed(ON_DESTROY)
    }

    override fun hideTransitionStarted() {
        screen.devLogD(TAG) { "hideTransitionStarted ${lifecycle.currentState}" }
        screenTransitionState.readyToBeResumed = false
        updateLifecycleIfNeed(ON_PAUSE)
    }

    override fun showTransitionFinished() {
        screen.devLogD(TAG) { "showTransitionFinished ${lifecycle.currentState}" }
        screenTransitionState.readyToBeResumed = true
        updateLifecycleIfNeed(ON_RESUME)
    }

    override fun toString(): String = "${ModoScreenAndroidAdapter::class.simpleName}, screenKey: ${screen.screenKey}"

    private fun onDispose() {
        screen.devLogI(TAG) { "onDispose. Clear ViewModelStore." }
        viewModelStore.clear()
    }

    private fun onCreate(savedState: Bundle?) {
        check(!isCreated) { "onCreate already called" }
        isCreated = true
        controller.performRestore(savedState)
        updateLifecycleIfNeed(ON_CREATE)
    }

    private fun performSave(outState: Bundle) {
        controller.performSave(outState)
    }

    /**
     * Provides essential Android Lifecycle and ViewModel composition locals for the screen:
     * - [LocalLifecycleOwner]
     * - [LocalViewModelStoreOwner]
     * - [LocalSavedStateRegistryOwner]
     *
     * This enables Jetpack Compose features that depend on these locals to work properly within the screen's scope.
     *
     * @param content The composable content that will have access to these composition locals
     */
    @Composable
    internal fun ProvideCompositionLocals(content: @Composable () -> Unit) {
        CompositionLocalProvider(
            LocalLifecycleOwner provides this,
            LocalViewModelStoreOwner provides this,
            LocalSavedStateRegistryOwner provides this,
            content = content
        )
    }

    /**
     * Capture value from [compositionLocal] to [atomicReference] when it enters the composition and clears it when lives or new value is provided.
     */
    @Composable
    private fun <T> DisposableAtomicReference(compositionLocal: CompositionLocal<T>, atomicReference: AtomicReference<T>) {
        val value = compositionLocal.current
        DisposableEffect(value) {
            atomicReference.compareAndSet(null, value)
            onDispose {
                atomicReference.set(null)
            }
        }
    }

    /**
     * Returns a unregister callback
     */
    private fun registerParentLifecycleListener(
        lifecycleOwner: LifecycleOwner?,
        observerFactory: () -> LifecycleObserver
    ): () -> Unit {
        if (lifecycleOwner != null) {
            val parentLifecycleObserver = observerFactory()
            val lifecycle = lifecycleOwner.lifecycle
            lifecycle.addObserver(parentLifecycleObserver)
            return {
                lifecycle.removeObserver(parentLifecycleObserver)
            }
        } else {
            return { }
        }
    }

    @Composable
    private fun LifecycleDisposableEffect(
        context: Context,
        parentLifecycleOwner: LifecycleOwner,
        manualResumePause: Boolean,
        content: @Composable () -> Unit
    ) {
        val activity = remember(context) {
            context.getActivity()
        }
        val savedState = rememberSaveable { Bundle() }
        if (!isCreated) {
            onCreate(savedState)
        }

        DisposableEffect(this) {
            handleLifecycleOnCompositionEnter(manualResumePause)
            onDispose { }
        }

        content()

        screen.SetupPreDispose()

        DisposableEffect(this) {
            screen.devLogV(TAG) { "LifecycleDisposableEffect parentLifecycleOwner: $parentLifecycleOwner" }

            val unregisterLifecycle = subscribeToParentLifecycle(
                parentLifecycleOwner = parentLifecycleOwner,
                savedState = savedState,
                isActivityFinishing = { activity?.isFinishing },
                isChangingConfigurations = { activity?.isChangingConfigurations }
            )

            onDispose {
                screen.devLogD(TAG) { "LifecycleDisposableEffect after content DisposableEffect.onDispose ${lifecycle.currentState}" }
                unregisterLifecycle()
                performSave(savedState)
                handleLifecycleOnCompositionExit(manualResumePause)
            }
        }
    }

    @VisibleForTesting
    internal fun handleLifecycleOnCompositionEnter(manualResumePause: Boolean) {
        updateLifecycleIfNeed(ON_START)
        if (!manualResumePause) {
            screenTransitionState.readyToBeResumed = true
            updateLifecycleIfNeed(ON_RESUME)
        }
    }

    @VisibleForTesting
    internal fun handleLifecycleOnCompositionExit(manualResumePause: Boolean) {
        if (!manualResumePause) {
            updateLifecycleIfNeed(ON_PAUSE)
        }
        updateLifecycleIfNeed(ON_STOP)
    }

    @VisibleForTesting
    internal fun subscribeToParentLifecycle(
        parentLifecycleOwner: LifecycleOwner,
        savedState: Bundle? = null,
        isActivityFinishing: () -> Boolean? = { null },
        isChangingConfigurations: () -> Boolean? = { null }
    ): () -> Unit = registerParentLifecycleListener(parentLifecycleOwner) {
        // If we still subscribed to parent lifecycle, then we in composition and content is visible
        LifecycleEventObserver { _, event ->
            if (event == ON_STOP && savedState != null) {
                performSave(savedState)
            }
            if (
                needPropagateLifecycleEventFromParent(
                    event,
                    isActivityFinishing = isActivityFinishing(),
                    isChangingConfigurations = isChangingConfigurations()
                )
            ) {
                updateLifecycleIfNeed(event)
            }
        }
    }

    /**
     * Attempts to update the screen's lifecycle state with the given event if all conditions are met.
     *
     * This method enforces several rules to ensure lifecycle integrity:
     * 1. **No resurrection**: Once DESTROYED, the lifecycle cannot move to any other state
     * 2. **No redundant events**: Events that would lead to an already-reached state are skipped
     * 3. **Transition readiness**: ON_RESUME requires [ScreenTransitionState.readyToBeResumed] to be true
     * 4. **Parent constraints**: Child state cannot exceed parent state (enforced by [parentStateAllowMove])
     * 5. **Single-step transitions**: State changes must be sequential (except CREATED -> DESTROYED)
     *
     * @param event The lifecycle event to potentially dispatch
     */
    @VisibleForTesting
    internal fun updateLifecycleIfNeed(event: Lifecycle.Event) {
        val parentState = atomicParentLifecycleOwner.get()?.lifecycle?.currentState
        if (
        // ignore any state updates if already destroyed, it cannot be moved up
            lifecycle.currentState != DESTROYED &&
            !stateAlreadyReached(lifecycle.currentState, event) &&
            // if ON_RESUME, then should be ready for it
            (event != ON_RESUME || screenTransitionState.readyToBeResumed) &&
            parentStateAllowMove(parentState, event)
        ) {
            assert(
                abs(lifecycle.currentState.ordinal - event.targetState.ordinal) == 1 ||
                    lifecycle.currentState == CREATED && event == ON_DESTROY
            ) {
                "Lifecycle state transition must be one step, but was ${lifecycle.currentState} -> $event"
            }
            screen.devLogD(TAG) { "safeHandleLifecycleEvent send $event" }
            lifecycle.handleLifecycleEvent(event)
        }
    }

    private data class ScreenTransitionState(
        @Volatile
        var readyToBeResumed: Boolean
    )

    companion object {

        private val moveLifecycleStateUpEvents = setOf(
            ON_CREATE,
            ON_START,
            ON_RESUME
        )

        private val moveLifecycleStateDownEvents = setOf(
            ON_STOP,
            ON_PAUSE,
            ON_DESTROY
        )

        private val TAG = ModoScreenAndroidAdapter::class.simpleName

        /**
         * Creates delegate for integration with android for the given [screen] or returns existed from cache.
         */
        @JvmStatic
        fun get(screen: Screen): ModoScreenAndroidAdapter =
            ScreenModelStore.getOrPutDependency(
                screen = screen,
                name = LifecycleDependency.KEY,
                onDispose = { it.onDispose() },
            ) { ModoScreenAndroidAdapter(screen) }

        @JvmStatic
        fun getOrNull(screen: Screen): ModoScreenAndroidAdapter? =
            ScreenModelStore.getDependencyOrNull(
                screen = screen,
                name = LifecycleDependency.KEY,
            )

        /**
         * Determines whether a lifecycle event from the parent should be propagated to the screen.
         *
         * Rules for propagation:
         * - **ON_DESTROY**: Only propagate when the activity is truly finishing, not during:
         *   - Configuration changes (isChangingConfigurations = true)
         *   - System-initiated process death (isActivityFinishing = false)
         *   This prevents SavedStateHandle crashes when the screen will be restored.
         *
         * - **ON_START**: Always propagate - if we're in composition, we should be at least STARTED
         *
         * - **ON_RESUME**: Always propagate to parent subscription, but [updateLifecycleIfNeed]
         *   makes the final decision based on [ScreenTransitionState.readyToBeResumed]
         *
         * - **Downward events** (ON_PAUSE, ON_STOP): Always propagate to ensure child state
         *   never exceeds parent state
         *
         * @param event The lifecycle event from the parent
         * @param isActivityFinishing True if the activity is finishing (user navigation back, finish() called)
         * @param isChangingConfigurations True if the activity is being recreated due to config change
         * @return true if the event should be propagated to the screen's lifecycle
         */
        @JvmStatic
        private fun needPropagateLifecycleEventFromParent(
            event: Lifecycle.Event,
            isActivityFinishing: Boolean?,
            isChangingConfigurations: Boolean?
        ) =
            // Propagate ON_DESTROY only when finishing happening.
            (event != ON_DESTROY || (isActivityFinishing != false && isChangingConfigurations != true)) &&
                // We can propagate ON_START because we are in composition, meaning we should be at least started
                (event == ON_START ||
                    // propagate ON_RESUME, but the final decision is up to updateLifecycleIfNeed
                    event == ON_RESUME ||
                    // Parent can always move down lifecycle to ensure children state is never greater than parent.
                    event in moveLifecycleStateDownEvents)

        /**
         * Checks if the target state of the given event has already been reached by the current state.
         *
         * This prevents redundant lifecycle events from being dispatched:
         * - For upward transitions (ON_CREATE, ON_START, ON_RESUME): Skip if target state <= current state
         *   Example: Skip ON_START when currentState is RESUMED
         * - For downward transitions (ON_PAUSE, ON_STOP, ON_DESTROY): Skip if target state >= current state
         *   Example: Skip ON_PAUSE when currentState is CREATED
         *
         * @param currentState The current lifecycle state
         * @param event The lifecycle event to check
         * @return true if the event's target state has already been reached and should be skipped
         */
        @JvmStatic
        internal fun stateAlreadyReached(currentState: Lifecycle.State, event: Lifecycle.Event) =
            // Skipping events that move the lifecycle state up, but this state is already reached.
            (event in moveLifecycleStateUpEvents && event.targetState <= currentState) ||
                // Skipping events that move the lifecycle state down, but this state is already reached.
                (event in moveLifecycleStateDownEvents && event.targetState >= currentState)

        /**
         * Validates that the parent's lifecycle state permits the requested state transition.
         *
         * Ensures the fundamental rule: **child state <= parent state**
         *
         * Always allows:
         * - Downward transitions (ON_PAUSE, ON_STOP): Parent can always downgrade children
         * - Transitions to CREATED or below: Initial states before parent dependency matters
         *
         * For upward transitions beyond CREATED (ON_START, ON_RESUME):
         * - Requires parent state >= target state
         * - Example: Cannot move to RESUMED if parent is only STARTED
         *
         * @param parentState The current state of the parent lifecycle owner, or null if not in composition
         * @param event The lifecycle event requesting a state change
         * @return true if the parent's state allows this transition
         */
        private fun parentStateAllowMove(
            parentState: Lifecycle.State?,
            event: Lifecycle.Event,
        ) =
            event in moveLifecycleStateDownEvents ||
                event.targetState <= CREATED ||
                parentState != null && parentState >= event.targetState
    }
}