package com.github.terrakok.modo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.Lifecycle.Event.ON_START
import androidx.lifecycle.Lifecycle.Event.ON_STOP
import com.github.terrakok.modo.android.ModoScreenAndroidAdapter
import com.github.terrakok.modo.android.overlaySaveableStateKey
import com.github.terrakok.modo.animation.ScreenTransition
import com.github.terrakok.modo.animation.cleanupProtectedScreens
import com.github.terrakok.modo.animation.preDisposeProtectedScreens
import com.github.terrakok.modo.lifecycle.LifecycleDependency
import com.github.terrakok.modo.logs.devLogI
import com.github.terrakok.modo.logs.devLogV
import com.github.terrakok.modo.model.ScreenModelStore
import com.github.terrakok.modo.model.dependenciesSortedByRemovePriority
import com.github.terrakok.modo.util.currentOrThrow

typealias RendererContent<State> = @Composable ComposeRendererScope<State>.(Modifier) -> Unit

typealias ContainerContent<State> = @Composable (Modifier) -> Unit

val defaultRendererContent: (@Composable ComposeRendererScope<*>.(screenModifier: Modifier) -> Unit) = { screenModifier ->
    screen.SaveableContent(screenModifier)
}

val LocalSaveableStateHolder = staticCompositionLocalOf<SaveableStateHolder?> { null }

private val LocalClearScreens = staticCompositionLocalOf<() -> Unit> {
    error("No LocalClearScreens provided!")
}

private val LocalPreDispose = staticCompositionLocalOf<() -> Unit> {
    error("No LocalPreDispose provided!")
}

private const val TAG = "ComposeRenderer"

internal inline val Screen.saveableStateKey: String get() = screenKey.value

/**
 * Provides integration of [Screen] to Modo's navigation system:
 * 1. Adds support of [rememberSaveable] by using [SaveableStateHolder.SaveableStateProvider] to store [Screen]'s state.
 * 2. Adds support of Android-related features, such as ViewModel, LifeCycle and SavedStateHandle.
 * 3. Handles lifecycle of [Screen] by adding [DisposableEffect] before and after content, in order to notify [ComposeRenderer]
 *    when [Screen.Content] is about to leave composition and when it has left composition.
 * @param modifier is a modifier that will be passed into [Screen.Content]
 * @param manualResumePause define whenever we are going to manually call [LifecycleDependency.showTransitionFinished] and [LifecycleDependency.hideTransitionStarted]
 * to emmit [ON_RESUME] and [ON_PAUSE]. Otherwise, [ON_RESUME] will be called straight after [ON_START] and [ON_PAUSE] will be called straight
 * before [ON_STOP].
 *
 * F.e. it is used by [ScreenTransition]:
 * + [ON_RESUME] emitted when animation of showing screen is finished
 * + [ON_PAUSE] emitted when animation of hiding screen is started
 */
@Composable
fun Screen.SaveableContent(
    modifier: Modifier = Modifier,
    manualResumePause: Boolean = false
) {
    LocalSaveableStateHolder.currentOrThrow.SaveableStateProvider(key = saveableStateKey) {
        SetupScreenCleanup()
        ModoScreenAndroidAdapter.get(this).ProvideAndroidIntegration(manualResumePause) {
            Content(modifier)
        }
    }
}

/**
 * Sets up safe screen cleanup management for this screen.
 *
 *  CRITICAL: Must be called BEFORE user content to ensure cleanup happens AFTER user content is gone.
 *
 * While this screen is in composition:
 * - Protects the screen from being prematurely cleaned by adding it to [cleanupProtectedScreens]
 * - Acts as a safety gate - [ComposeRenderer.clearScreens] will skip this screen while it's tracked here
 *
 * When this screen leaves composition:
 * - Removes protection by removing it from [cleanupProtectedScreens]
 * - Triggers immediate cleanup of screen resources (ScreenModelStore, SavedState) via [LocalClearScreens]
 *
 * @see ComposeRenderer.clearScreens for the cleanup logic that respects this protection
 */
@Composable
internal inline fun Screen.SetupScreenCleanup() {
    val clearScreens = LocalClearScreens.current
    DisposableEffect(this) {
        devLogV(TAG) { "SetupScreenCleanup DisposableEffect" }
        cleanupProtectedScreens[this@SetupScreenCleanup] = Unit
        onDispose {
            cleanupProtectedScreens -= this@SetupScreenCleanup
            devLogV(TAG) { "SetupScreenCleanup DisposableEffect.onDispose" }
            clearScreens.invoke()
        }
    }
}

/**
 * Sets up protection for this screen during the disposal phase to prevent premature lifecycle cleanup.
 *
 * While this screen is in composition:
 * - Protects the screen from being prematurely disposed by adding it to [preDisposeProtectedScreens]
 * - Acts as a safety gate - [ComposeRenderer.onPreDispose] will skip this screen while it's tracked here
 *
 * When this screen leaves composition:
 * - Removes protection by removing it from [preDisposeProtectedScreens]
 * - Triggers pre-disposal lifecycle callback via [LocalPreDispose]
 *
 * @see ComposeRenderer.onPreDispose for the pre-disposal logic that respects this protection
 */
@Composable
internal inline fun Screen.SetupPreDispose() {
    val onPreDispose = LocalPreDispose.current
    DisposableEffect(this) {
        devLogV(TAG) { "SetupLifecycleDisposal DisposableEffect" }
        preDisposeProtectedScreens[this@SetupPreDispose] = Unit
        onDispose {
            devLogV(TAG) { "SetupLifecycleDisposal DisposableEffect.onDispose" }
            preDisposeProtectedScreens -= this@SetupPreDispose
            onPreDispose()
        }
    }
}

@Deprecated("Is going to be removed in 0.12.0")
class ComposeRendererScope<State : NavigationState>(
    val oldState: State?,
    val newState: State?,
    val screen: Screen,
)

/**
 * Renderer responsibilities:
 *  1. Rendering - wrapping state to composable state and delegating rendering to screens
 *  2. Storing and clearing composable states inside [SaveableStateHolder]
 */
internal class ComposeRenderer<State : NavigationState>(
    private val containerScreen: ContainerScreen<*, *>,
    initialState: State
) : NavigationRenderer<State> {

    val _composeState: MutableState<State> = mutableStateOf(initialState, neverEqualPolicy())
    val composeState: androidx.compose.runtime.State<State> get() = _composeState
    private var lastState: State? = null
    var state: State by _composeState
        private set

    // TODO: share removed screen for whole structure?
    private val removedScreens = mutableSetOf<Screen>()

    // TODO: move logic if updating state to navModel. For removedScreens just subscribe to it from here.
    override fun render(state: State) {
        this.state.let { currentState ->
            removedScreens.addAll(calculateRemovedScreens(currentState, state))
        }
        lastState = this.state
        this.state = state
        // Handling a case when updating state doesn't cause UI to update. But if some screens was removed, we need to move them to destroy state.
        // F.e. removing previous screen causes this case.
        onPreDispose()
    }

    @Suppress("UnusedPrivateProperty", "SpreadOperator")
    @Composable
    fun Content(
        screen: Screen,
        modifier: Modifier = Modifier,
        provideCompositionLocal: Array<ProvidedValue<*>> = emptyArray(),
        content: RendererContent<State> = defaultRendererContent
    ) {
        val stateHolder: SaveableStateHolder = LocalSaveableStateHolder.currentOrThrow

        val clearScreens = remember(stateHolder) {
            {
                clearScreens(stateHolder)
            }
        }

        // pre dispose means that we can send ON_DISPOSE if screen is removing,
        // to let Screen.Content to handle ON_DISPOSE by using functions like DisposableEffect
        val preDispose = remember {
            {
                onPreDispose()
            }
        }

        CompositionLocalProvider(
            LocalContainerScreen provides containerScreen,
            LocalClearScreens provides clearScreens,
            LocalPreDispose provides preDispose,
            *provideCompositionLocal
        ) {
            ComposeRendererScope(lastState, state, screen).content(modifier)
        }
    }

    @Suppress("UnusedPrivateProperty", "SpreadOperator")
    @Composable
    fun ContentNew(
        modifier: Modifier = Modifier,
        provideCompositionLocal: Array<ProvidedValue<*>> = emptyArray(),
        content: ContainerContent<State>
    ) {
        val stateHolder: SaveableStateHolder = LocalSaveableStateHolder.currentOrThrow

        val clearScreens = remember(stateHolder) {
            {
                clearScreens(stateHolder)
            }
        }

        // pre dispose means that we can send ON_DISPOSE if screen is removing,
        // to let Screen.Content to handle ON_DISPOSE by using functions like DisposableEffect
        val preDispose = remember {
            {
                onPreDispose()
            }
        }

        CompositionLocalProvider(
            LocalContainerScreen provides containerScreen,
            LocalClearScreens provides clearScreens,
            LocalPreDispose provides preDispose,
            *provideCompositionLocal
        ) {
            content(modifier)
        }
    }

    /**
     * Clear states of removed screens from given [stateHolder].
     * @param stateHolder - SaveableStateHolder that contains screen states
     * @param clearAll - forces to remove all screen states that renderer holds (removed and "displayed")
     */
    private fun clearScreens(stateHolder: SaveableStateHolder, clearAll: Boolean = false) {
        fun Iterable<Screen>.clearStates(stateHolder: SaveableStateHolder) = forEach { screen ->
            screen.clearState(stateHolder)
        }

        if (clearAll) {
            state?.getChildScreens()?.clearStates(stateHolder)
        }
        // There can be several transition of different screens on the screen,
        // so it is important properly clear screens that are not visible for user.
        val safeToRemove = removedScreens.filter { it !in cleanupProtectedScreens }
        safeToRemove.clearStates(stateHolder)
        if (removedScreens.isNotEmpty()) {
            safeToRemove.forEach {
                removedScreens -= it
            }
        }
    }

    /**
     * Called onPreDispose for removed screens, that are not presented in [preDisposeProtectedScreens] (not displayed on screen).
     * @param clearAll - forces to call onPreDispose on all children screen states that renderer holds (removed and "displayed")
     */
    private fun onPreDispose(clearAll: Boolean = false) {
        fun Iterable<Screen>.onPreDispose() = forEach { screen ->
            screen.onPreDispose()
        }

        if (clearAll) {
            state?.getChildScreens()?.onPreDispose()
        }
        // There can be several transition of different screens on the screen,
        // so it is important properly clear screens that are not visible for user.
        val safeToRemove = removedScreens.filter { it !in preDisposeProtectedScreens }
        safeToRemove.onPreDispose()
    }

    private fun Screen.clearState(stateHolder: SaveableStateHolder) {
        // It's important to do this check for debug purpose, because we must guaranty that Screen is cleaned only if it is not displaying anymore.
        // But it seems like it is not working with movable content, so this one is going to be triggered.
        if (this in cleanupProtectedScreens) {
            ModoDevOptions.onIllegalClearState.validationFailed(
                IllegalStateException(
                    "Trying to remove clean state of the screen $this, why this screen still is visible for User."
                )
            )
        }
        ScreenModelStore.remove(this)
        stateHolder.removeState(saveableStateKey)
        stateHolder.removeState(overlaySaveableStateKey)

        ModoDevOptions.onScreenDisposeListener?.invoke(this)
        // clear nested screens using recursion
        ((this as? ContainerScreen<*, *>)?.renderer as? ComposeRenderer<*>)?.clearScreens(stateHolder, clearAll = true)
    }

    // need for correct handling lifecycle
    private fun Screen.onPreDispose() {
        devLogI(TAG) { "onPreDispose $screenKey" }
        dependenciesSortedByRemovePriority()
            .filterIsInstance<LifecycleDependency>()
            .forEach { it.onPreDispose() }
        // send onPreDispose to nested screens
        ((this as? ContainerScreen<*, *>)?.renderer as? ComposeRenderer<*>)?.onPreDispose(clearAll = true)
    }

    private fun calculateRemovedScreens(oldState: NavigationState, newState: NavigationState): List<Screen> {
        val newChainSet = newState.getChildScreens()
        return oldState.getChildScreens().filter { it !in newChainSet }
    }

}