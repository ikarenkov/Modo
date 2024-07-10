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

    private fun onCreate(savedState: Bundle?) {
        check(!isCreated) { "onCreate already called" }
        isCreated = true
        controller.performRestore(savedState)
        initEvents.forEach {
            lifecycle.safeHandleLifecycleEvent(it)
        }
    }

    private fun emitOnStartEvents() {
        startEvents.forEach {
            lifecycle.safeHandleLifecycleEvent(it)
        }
    }

    private fun emitOnStopEvents() {
        stopEvents.forEach {
            lifecycle.safeHandleLifecycleEvent(it)
        }
    }

    @Composable
    fun ProvideAndroidIntegration(
        content: @Composable () -> Unit
    ) {
        val context: Context = LocalContext.current
        val parentLifecycleOwner = LocalLifecycleOwner.current
        LifecycleDisposableEffect(context, parentLifecycleOwner) {
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
        disposeEvents.forEach { event ->
            lifecycle.safeHandleLifecycleEvent(event)
        }
    }

    @Suppress("UnusedParameter")
    private fun onDispose() {
//        Log.d("LifecycleDebug", "${screen.screenKey} ModoScreenAndroidAdapter.onDispose. Clear ViewModelStore.")
        viewModelStore.clear()
    }

    override fun toString(): String = "${ModoScreenAndroidAdapter::class.simpleName}, screenKey: ${screen.screenKey}"

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
        content: @Composable () -> Unit
    ) {
        val activity = remember(context) {
            context.getActivity()
        }
        val savedState = rememberSaveable { Bundle() }
        if (!isCreated) {
            onCreate(savedState) // do this in the UI thread to force it to be called before anything else
        }

        content()

        DisposableEffect(this) {
            val unregisterLifecycle = registerParentLifecycleListener(parentLifecycleOwner) {
                LifecycleEventObserver { owner, event ->
                    when {
                        /**
                         * Instance of the screen isn't recreated during config changes so skip this event
                         * to avoid crash while accessing to ViewModel with SavedStateHandle, because after
                         * ON_DESTROY, [androidx.lifecycle.SavedStateHandleController] is marked as not
                         * attached and next call of [registerSavedStateProvider] after recreating Activity
                         * on the same instance causing the crash.
                         *
                         * Also when activity is destroyed, but not finished, screen is not destroyed.
                         *
                         * In the case of Fragments, we unsubscribe before ON_DESTROY event, so there is no problem with this.
                         */
                        event == Lifecycle.Event.ON_DESTROY && (activity?.isFinishing == false || activity?.isChangingConfigurations == true) ->
                            return@LifecycleEventObserver
                        // when the Application goes to background, perform save
                        event == Lifecycle.Event.ON_STOP ->
                            performSave(savedState)
                    }
                    lifecycle.safeHandleLifecycleEvent(event)
                }
            }

            emitOnStartEvents()

            onDispose {
//                Log.d("LifecycleDebug", "ModoScreenAndroidAdapter registerParentLifecycleListener onDispose ${screen.screenKey}")
                unregisterLifecycle()
                // when the screen goes to stack, perform save
                performSave(savedState)
                // notify lifecycle screen listeners
                emitOnStopEvents()
            }
        }
    }

    private fun LifecycleRegistry.safeHandleLifecycleEvent(event: Lifecycle.Event) {
        val currentState = currentState
        val skippEvent = !currentState.isAtLeast(Lifecycle.State.INITIALIZED) ||
            // Protection from double event sending from the parent
            ((event in startEvents || event in initEvents) && event.targetState <= currentState) ||
            (event in stopEvents && event.targetState >= currentState)

        // For debugging
//        Log.d("ModoScreenAndroidAdapter", "safeHandleLifecycleEvent ${screen.screenKey} $event")
        if (!skippEvent) {
            handleLifecycleEvent(event)
        }
    }

    companion object {

        private val initEvents = arrayOf(
            Lifecycle.Event.ON_CREATE
        )

        private val startEvents = arrayOf(
            Lifecycle.Event.ON_START,
            Lifecycle.Event.ON_RESUME
        )

        private val stopEvents = arrayOf(
            Lifecycle.Event.ON_PAUSE,
            Lifecycle.Event.ON_STOP
        )

        private val disposeEvents = arrayOf(
            Lifecycle.Event.ON_DESTROY
        )

        /**
         * Creates delegate for integration with android for the given [screen] or returns existed from cache.
         */
        fun get(screen: Screen): ModoScreenAndroidAdapter =
            ScreenModelStore.getOrPutDependency(
                screen = screen,
                name = "AndroidScreenLifecycleOwner",
                onDispose = { it.onDispose() },
            ) { ModoScreenAndroidAdapter(screen) }
    }
}