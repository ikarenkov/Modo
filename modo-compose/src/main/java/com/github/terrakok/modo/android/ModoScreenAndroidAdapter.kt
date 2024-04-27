package com.github.terrakok.modo.android

import android.app.Activity
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
import com.github.terrakok.modo.model.ScreenModelStore
import com.github.terrakok.modo.util.getActivity
import com.github.terrakok.modo.util.getApplication
import java.util.concurrent.atomic.AtomicReference

/**
 * Adapter to link modo with android. It the single instance of [ModoScreenAndroidAdapter] per Screen.
 */
class ModoScreenAndroidAdapter private constructor() :
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner,
    HasDefaultViewModelProviderFactory {

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

    private val atomicContext = AtomicReference<Context>()
    private val atomicParentLifecycleOwner = AtomicReference<LifecycleOwner>()
    private val controller = SavedStateRegistryController.create(this)
    private var isCreated: Boolean by mutableStateOf(false)

    private val activity: Activity? get() = atomicContext.get()?.getActivity()
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
        LifecycleDisposableEffect()

        CompositionLocalProvider(*getProviders().toTypedArray()) {
            content()
        }
    }

    fun onDispose(screen: Screen) {
        viewModelStore.clear()
        disposeEvents.forEach { event ->
            lifecycle.safeHandleLifecycleEvent(event)
        }
    }

    private fun performSave(outState: Bundle) {
        controller.performSave(outState)
    }

    @Composable
    private fun getProviders(): List<ProvidedValue<*>> {
        disposableAtomicReference(LocalContext, atomicContext)
        disposableAtomicReference(LocalLifecycleOwner, atomicParentLifecycleOwner)

        return remember(this) {
            listOf(
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
    private fun <T> disposableAtomicReference(compositionLocal: CompositionLocal<T>, atomicReference: AtomicReference<T>) {
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
        observerFactory: () -> LifecycleObserver
    ): () -> Unit {
        val lifecycleOwner = atomicParentLifecycleOwner.get()
        if (lifecycleOwner != null) {
            val parentLifecycleObserver = observerFactory()
            val lifecycle = lifecycleOwner.lifecycle
            lifecycle.addObserver(parentLifecycleObserver)
            return { lifecycle.removeObserver(parentLifecycleObserver) }
        } else {
            return { }
        }
    }

    @Composable
    private fun LifecycleDisposableEffect() {
        val savedState = rememberSaveable { Bundle() }
        if (!isCreated) {
            onCreate(savedState) // do this in the UI thread to force it to be called before anything else
        }

        DisposableEffect(this) {
            val unregisterLifecycle = registerParentLifecycleListener {
                LifecycleEventObserver { _, event ->
                    when {
                        /**
                         * Instance of the screen isn't recreated during config changes so skip this event
                         * to avoid crash while accessing to ViewModel with SavedStateHandle, because after
                         * ON_DESTROY, [androidx.lifecycle.SavedStateHandleController] is marked as not
                         * attached and next call of [registerSavedStateProvider] after recreating Activity
                         * on the same instance causing the crash.
                         *
                         * Also when activity is destroyed, but not finished, screen is not destroyed.
                         */
                        event == Lifecycle.Event.ON_DESTROY && activity?.isFinishing == false ->
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
        if (!currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            return
        }
        // Protection from double event sending from the parent
        if (event in startEvents && event.targetState <= currentState) {
            return
        }
        if (event in stopEvents && event.targetState >= currentState) {
            return
        }

        // For debugging
//        Log.d("ModoScreenAndroidAdapter", "safeHandleLifecycleEvent ${screen.screenKey} $event")
        handleLifecycleEvent(event)
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
                onDispose = { it.onDispose(screen) }
            ) { ModoScreenAndroidAdapter() }
    }
}