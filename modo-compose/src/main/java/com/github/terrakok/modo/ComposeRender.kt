package com.github.terrakok.modo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
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
import com.github.terrakok.modo.animation.ScreenTransition
import com.github.terrakok.modo.animation.displayingScreensAfterScreenContent
import com.github.terrakok.modo.animation.displayingScreensBeforeScreenContent
import com.github.terrakok.modo.lifecycle.LifecycleDependency
import com.github.terrakok.modo.model.ScreenModelStore
import com.github.terrakok.modo.model.dependenciesSortedByRemovePriority
import com.github.terrakok.modo.util.currentOrThrow
import kotlinx.coroutines.channels.Channel

typealias RendererContent<State> = @Composable ComposeRendererScope<State>.(Modifier) -> Unit

val defaultRendererContent: (@Composable ComposeRendererScope<*>.(screenModifier: Modifier) -> Unit) = { screenModifier ->
    screen.SaveableContent(screenModifier)
}

val LocalSaveableStateHolder = staticCompositionLocalOf<SaveableStateHolder?> { null }

private val LocalBeforeScreenContentOnDispose = staticCompositionLocalOf<() -> Unit> {
    error("No LocalBeforeScreenContentOnDispose provided!")
}

private val LocalAfterScreenContentOnDispose = staticCompositionLocalOf<() -> Unit> {
    error("No LocalAfterScreenContentOnDispose provided!")
}

/**
 * Provides integration of [Screen] to Modo's navigation system:
 * 1. Adds support of [rememberSaveable] by using [SaveableStateHolder.SaveableStateProvider] to store [Screen]'s state.
 * 2. Adds support of Android-related features, such as ViewModel, LifeCycle and SavedStateHandle.
 * 3. Handles lifecycle of [Screen] by adding [DisposableEffect] before and after content, in order to notify [ComposeRenderer]
 *    when [Screen.Content] is about to leave composition and when it has left composition.
 * @param modifier is a modifier that will be passed into [Screen.Content]
 * @param manualResumePause define whenever we are going to manually call [LifecycleDependency.onResume] and [LifecycleDependency.onPause]
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
    LocalSaveableStateHolder.currentOrThrow.SaveableStateProvider(key = screenKey) {
        ModoScreenAndroidAdapter.get(this).ProvideAndroidIntegration(manualResumePause) {
            BeforeScreenContent()
            Content(modifier)
            AfterScreenContent()
        }
    }
}

/**
 * This function responsible for correct cleaning [Screen]'s when it already has left composition and some clean up can be made.
 * It's not always clean [Screen] when it lives composition, but adds extra logic of tracking displaying [Screen]'s and triggering
 * function provided by [LocalBeforeScreenContentOnDispose] in order to try clean removed screens that are not visible for user.
 */
@Composable
private inline fun Screen.BeforeScreenContent() {
    val onDisposed = LocalBeforeScreenContentOnDispose.current
    DisposableEffect(this) {
        displayingScreensBeforeScreenContent[this@BeforeScreenContent] = Unit
        onDispose {
            displayingScreensBeforeScreenContent -= this@BeforeScreenContent
//            Log.d("LifecycleDebug", "BeforeScreenContent $screenKey onDispose")
            onDisposed.invoke()
        }
    }
}

@Composable
private inline fun Screen.AfterScreenContent() {
    val onPreDispose = LocalAfterScreenContentOnDispose.current
    DisposableEffect(this) {
        displayingScreensAfterScreenContent[this@AfterScreenContent] = Unit
        onDispose {
            displayingScreensAfterScreenContent -= this@AfterScreenContent
//            Log.d("LifecycleDebug", "AfterScreenContent $screenKey onDispose")
            onPreDispose()
        }
    }
}

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
) : NavigationRenderer<State> {

    /**
     * A channel that is used to notify about completing of screen transition, so we can dispose
     * screen that is removed out of the backstack properly.
     */
    val transitionCompleteChannel: Channel<Unit> = Channel(Channel.UNLIMITED)

    private var lastState: State? = null
    var state: State? by mutableStateOf(null, neverEqualPolicy())
        private set

    // TODO: share removed screen for whole structure?
    private val removedScreens = mutableSetOf<Screen>()

    override fun render(state: State) {
        this.state?.let { currentState ->
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

        val beforeScreenContentOnDispose = remember {
            {
                clearScreens(stateHolder)
            }
        }

        // pre dispose means that we can send ON_DISPOSE if screen is removing,
        // to let Screen.Content to handle ON_DISPOSE by using functions like DisposableEffect
        val afterScreenContentOnDispose = remember {
            {
                onPreDispose()
            }
        }

        CompositionLocalProvider(
            LocalContainerScreen provides containerScreen,
            LocalBeforeScreenContentOnDispose provides beforeScreenContentOnDispose,
            LocalAfterScreenContentOnDispose provides afterScreenContentOnDispose,
            *provideCompositionLocal
        ) {
            ComposeRendererScope(lastState, state, screen).content(modifier)
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
        val safeToRemove = removedScreens.filter { it !in displayingScreensBeforeScreenContent }
        safeToRemove.clearStates(stateHolder)
        if (removedScreens.isNotEmpty()) {
            safeToRemove.forEach {
                removedScreens -= it
            }
        }
    }

    /**
     * Called onPreDispose for removed screens, that are not presented in [displayingScreensAfterScreenContent] (not displayed on screen).
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
        val safeToRemove = removedScreens.filter { it !in displayingScreensAfterScreenContent }
        safeToRemove.onPreDispose()
    }

    private fun Screen.clearState(stateHolder: SaveableStateHolder) {
        // It's important to do this check for debug purpose, because we must guaranty that Screen is cleaned only if it is not displaying anymore.
        // But it seems like it is not working with movable content, so this one is going to be triggered.
        if (this in displayingScreensBeforeScreenContent) {
            ModoDevOptions.onIllegalClearState.validationFailed(
                IllegalStateException(
                    "Trying to remove clean state of the screen $this, why this screen still is visible for User."
                )
            )
        }
        ScreenModelStore.remove(this)
        stateHolder.removeState(screenKey)
        // clear nested screens using recursion
        ((this as? ContainerScreen<*, *>)?.renderer as? ComposeRenderer<*>)?.clearScreens(stateHolder, clearAll = true)
    }

    // need for correct handling lifecycle
    private fun Screen.onPreDispose() {
//        Log.d("LifecycleDebug", "afterScreenContentOnDispose $screenKey")
        dependenciesSortedByRemovePriority()
            .filterIsInstance<LifecycleDependency>()
            .forEach { it.onPreDispose() }
        // send afterScreenContentOnDispose to nested screens
        ((this as? ContainerScreen<*, *>)?.renderer as? ComposeRenderer<*>)?.onPreDispose(clearAll = true)
    }

    private fun calculateRemovedScreens(oldState: NavigationState, newState: NavigationState): List<Screen> {
        val newChainSet = newState.getChildScreens()
        return oldState.getChildScreens().filter { it !in newChainSet }
    }

}