package com.github.terrakok.modo.android

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocal
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSavedStateRegistryOwner
import androidx.lifecycle.HasDefaultViewModelProviderFactory
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.Event.ON_CREATE
import androidx.lifecycle.Lifecycle.Event.ON_DESTROY
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
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
import androidx.lifecycle.enableSavedStateHandles
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.lifecycle.LifecycleDependency
import com.github.terrakok.modo.model.ScreenModelStore
import com.github.terrakok.modo.model.ScreenModelStore.remove
import com.github.terrakok.modo.util.getActivity
import com.github.terrakok.modo.util.getApplication
import java.util.concurrent.atomic.AtomicReference

/**
 * Adapter for Screem, to support android-related features using Modo, such as:
 * 1. ViewModel support
 * 2. Lifecycle support
 * 3. SavedState support
 *
 * It the single instance of [ModoScreenAndroidAdapter] per Screen.
 */
class ModoScreenAndroidAdapter private constructor(
//    just for debug purpose.
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
    private val atomicParentLifecycleOwner = AtomicReference<LifecycleOwner>()
    private val application: Application? get() = atomicContext.get()?.applicationContext?.getApplication()

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
        LifecycleDisposableEffect(context, parentLifecycleOwner, manualResumePause) {
            @Suppress("SpreadOperator")
            CompositionLocalProvider(*getProviders()) {
                content()
            }
        }
    }

    /**
     * Must be called before [remove] to inform that this screen is going to be removed.
     * We need it to provide support of using DisposableEffect or/and LaunchedEffect inside [Screen.Content].
     * F.e. to be able to collect ON_DISPOSE lifecycle event.
     */
    override fun onPreDispose() {
//        Log.d("LifecycleDebug", "${screen.screenKey} ModoScreenAndroidAdapter.onPreDispose, emit ON_DESTROY event.")
        safeHandleLifecycleEvent(ON_DESTROY)
    }

    override fun onPause() {
        safeHandleLifecycleEvent(ON_PAUSE)
    }

    override fun onResume() {
        safeHandleLifecycleEvent(ON_RESUME)
    }

    override fun toString(): String = "${ModoScreenAndroidAdapter::class.simpleName}, screenKey: ${screen.screenKey}"

    @Suppress("UnusedParameter")
    private fun onDispose() {
//        Log.d("LifecycleDebug", "${screen.screenKey} ModoScreenAndroidAdapter.onDispose. Clear ViewModelStore.")
        viewModelStore.clear()
    }

    private fun onCreate(savedState: Bundle?) {
        check(!isCreated) { "onCreate already called" }
        isCreated = true
        controller.performRestore(savedState)
        safeHandleLifecycleEvent(ON_CREATE)
    }

    private fun performSave(outState: Bundle) {
        controller.performSave(outState)
    }

    @Composable
    private fun getProviders(): Array<ProvidedValue<*>> {
        DisposableAtomicReference(LocalContext, atomicContext)
        DisposableAtomicReference(LocalLifecycleOwner, atomicParentLifecycleOwner)

        return remember(this) {
            arrayOf(
                LocalLifecycleOwner provides this,
                LocalViewModelStoreOwner provides this,
                LocalSavedStateRegistryOwner provides this
            )
        }
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
            onCreate(savedState) // do this in the UI thread to force it to be called before anything else
        }

        DisposableEffect(this) {
            safeHandleLifecycleEvent(ON_START)
            if (!manualResumePause) {
                safeHandleLifecycleEvent(ON_RESUME)
            }
            onDispose { }
        }

        content()

        DisposableEffect(this) {
            val unregisterLifecycle = registerParentLifecycleListener(parentLifecycleOwner) {
                LifecycleEventObserver { _, event ->
                    // when the Application goes to background, perform save
                    if (event == ON_STOP) {
                        performSave(savedState)
                    }
                    if (
                        needPropagateLifecycleEventFromParent(
                            event,
                            isActivityFinishing = activity?.isFinishing,
                            isChangingConfigurations = activity?.isChangingConfigurations
                        )
                    ) {
                        safeHandleLifecycleEvent(event)
                    }
                }
            }

            onDispose {
//                Log.d("LifecycleDebug", "ModoScreenAndroidAdapter registerParentLifecycleListener onDispose ${screen.screenKey}")
                unregisterLifecycle()
                // when the screen goes to stack, perform save
                performSave(savedState)
                // notify lifecycle screen listeners
                if (!manualResumePause) {
                    safeHandleLifecycleEvent(ON_PAUSE)
                }
                safeHandleLifecycleEvent(ON_STOP)
            }
        }
    }

    private fun safeHandleLifecycleEvent(event: Lifecycle.Event) {
        val skippEvent = needSkipEvent(lifecycle.currentState, event)
        if (!skippEvent) {
//            Log.d("ModoScreenAndroidAdapter", "${screen.screenKey} handleLifecycleEvent $event")
            lifecycle.handleLifecycleEvent(event)
        }
    }

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
        fun needPropagateLifecycleEventFromParent(
            event: Lifecycle.Event,
            isActivityFinishing: Boolean?,
            isChangingConfigurations: Boolean?
        ) =
            /*
             * Instance of the screen isn't recreated during config changes so skip this event
             * to avoid crash while accessing to ViewModel with SavedStateHandle, because after
             * ON_DESTROY, [androidx.lifecycle.SavedStateHandleController] is marked as not
             * attached and next call of [registerSavedStateProvider] after recreating Activity
             * on the same instance causing the crash.
             *
             * Also, when activity is destroyed, but not finished, screen is not destroyed.
             *
             * In the case of Fragments, we unsubscribe before ON_DESTROY event, so there is no problem with this.
             */
            if (event == ON_DESTROY && (isActivityFinishing == false || isChangingConfigurations == true)) {
                false
            } else {
                // Parent can only move lifecycle state down. Because parent cant be already resumed, but child is not, because of running animation.
                event !in moveLifecycleStateUpEvents
            }

        @JvmStatic
        internal fun needSkipEvent(currentState: Lifecycle.State, event: Lifecycle.Event) =
            !currentState.isAtLeast(Lifecycle.State.INITIALIZED) ||
                // Skipping events that moves lifecycle state up, but this state is already reached.
                (event in moveLifecycleStateUpEvents && event.targetState <= currentState) ||
                // Skipping events that moves lifecycle state down, but this state is already reached.
                (event in moveLifecycleStateDownEvents && event.targetState >= currentState)
    }
}