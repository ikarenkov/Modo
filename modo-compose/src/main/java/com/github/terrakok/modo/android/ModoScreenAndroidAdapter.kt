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
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import androidx.lifecycle.LifecycleOwner
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
import com.github.terrakok.modo.lifecycle.LifecycleDependency
import com.github.terrakok.modo.logs.devLogD
import com.github.terrakok.modo.logs.devLogI
import com.github.terrakok.modo.logs.devLogV
import com.github.terrakok.modo.model.ScreenModelStore
import com.github.terrakok.modo.model.ScreenModelStore.remove
import com.github.terrakok.modo.util.getActivity
import com.github.terrakok.modo.util.getApplication
import java.util.concurrent.atomic.AtomicReference

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

    @VisibleForTesting
    internal val lifecycleManager = ScreenLifecycleManager(this)

    override val lifecycle get() = lifecycleManager.lifecycle

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
        DisposableAtomicReference(LocalContext, atomicContext)
        DisposableAtomicReference(LocalLifecycleOwner, lifecycleManager.parentLifecycleOwner)
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
        lifecycleManager.updateLifecycleIfNeeded(Lifecycle.Event.ON_DESTROY)
    }

    override fun hideTransitionStarted() {
        screen.devLogD(TAG) { "hideTransitionStarted ${lifecycle.currentState}" }
        lifecycleManager.hideTransitionStarted()
    }

    override fun showTransitionFinished() {
        screen.devLogD(TAG) { "showTransitionFinished ${lifecycle.currentState}" }
        lifecycleManager.showTransitionFinished()
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
        lifecycleManager.updateLifecycleIfNeeded(Lifecycle.Event.ON_CREATE)
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
            lifecycleManager.handleCompositionEnter(manualResumePause)
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
                lifecycleManager.handleCompositionExit(manualResumePause)
            }
        }
    }

    @VisibleForTesting
    internal fun subscribeToParentLifecycle(
        parentLifecycleOwner: LifecycleOwner,
        savedState: Bundle? = null,
        isActivityFinishing: () -> Boolean? = { null },
        isChangingConfigurations: () -> Boolean? = { null }
    ): () -> Unit = lifecycleManager.subscribeToParentLifecycle(
        parentLifecycleOwner = parentLifecycleOwner,
        isActivityFinishing = isActivityFinishing,
        isChangingConfigurations = isChangingConfigurations,
        onEventBeforePropagation = { event ->
            // Handle SavedState side-effect (adapter's responsibility)
            if (event == ON_STOP && savedState != null) {
                performSave(savedState)
            }
        }
    )

    companion object {

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
    }
}