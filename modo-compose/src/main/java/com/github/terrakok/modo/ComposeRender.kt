package com.github.terrakok.modo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.github.terrakok.modo.android.ModoScreenAndroidAdapter
import com.github.terrakok.modo.model.ScreenModelStore
import com.github.terrakok.modo.util.currentOrThrow
import kotlinx.coroutines.channels.Channel

typealias RendererContent<State> = @Composable ComposeRendererScope<State>.() -> Unit

val defaultRendererContent: (@Composable ComposeRendererScope<*>.() -> Unit) = {
    screen.SaveableContent()
    val channel = LocalTransitionCompleteChannel.current
    // There's no animation, we can instantly mark the transition as completed
    DisposableEffect(screen.screenKey) {
        onDispose {
            channel.trySend(Unit)
        }
    }
}

val LocalSaveableStateHolder = staticCompositionLocalOf<SaveableStateHolder?> { null }

/**
 * You can receive channel from it to inform Modo that your custom [ContainerScreen] finished transition and screen can be safely removed.
 */
val LocalTransitionCompleteChannel = staticCompositionLocalOf<Channel<Unit>> { error("no channel provided") }

@Composable
fun Screen.SaveableContent() {
    LocalSaveableStateHolder.currentOrThrow.SaveableStateProvider(key = screenKey) {
        ModoScreenAndroidAdapter.get(this).ProvideAndroidIntegration {
            Content()
        }
    }
}

@Composable
fun Screen.SaveableContentWithAndroidIntegration() {
    LocalSaveableStateHolder.currentOrThrow.SaveableStateProvider(key = screenKey) {
        ModoScreenAndroidAdapter.get(this).ProvideAndroidIntegration {
            Content()
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
    private val containerScreen: ContainerScreen<*>,
) : NavigationRenderer<State> {

    /**
     * A channel that is used to notify about completing of screen transition, so we can dispose
     * screen that is removed out of the backstack properly.
     */
    val transitionCompleteChannel: Channel<Unit> = Channel(Channel.CONFLATED)

    private var lastState: State? = null
    var state: State? by mutableStateOf(null, neverEqualPolicy())
        private set

    // TODO: share removed screen for whole structure
    private val removedScreens = mutableSetOf<Screen>()

    override fun render(state: State) {
        this.state?.let { currentState ->
            removedScreens.addAll(calculateRemovedScreens(currentState, state))
        }
        lastState = this.state
        this.state = state
    }

    @Composable
    fun Content(
        screen: Screen,
        content: RendererContent<State> = defaultRendererContent
    ) {
        val stateHolder: SaveableStateHolder = LocalSaveableStateHolder.currentOrThrow
        LaunchedEffect(screen.screenKey) {
            for (event in transitionCompleteChannel) {
                clearScreens(stateHolder)
            }
        }
        CompositionLocalProvider(
            LocalContainerScreen provides containerScreen,
            LocalTransitionCompleteChannel provides transitionCompleteChannel,
        ) {
            ComposeRendererScope(lastState, state, screen).content()
        }
    }

    /**
     * Clear states of removed screens from given [stateHolder].
     * @param stateHolder - SaveableStateHolder that contains screen states
     * @param clearAll - forces to remove all screen states that renderer holds (removed and "displayed")
     */
    private fun clearScreens(stateHolder: SaveableStateHolder, clearAll: Boolean = false) {
        if (clearAll) {
            state?.getChildScreens()?.clearStates(stateHolder)
        }
        removedScreens.clearStates(stateHolder)
        if (removedScreens.isNotEmpty()) {
            removedScreens.clear()
        }
    }

    private fun Iterable<Screen>.clearStates(stateHolder: SaveableStateHolder) = forEach { screen ->
        ScreenModelStore.remove(screen)
        stateHolder.removeState(screen.screenKey)
        // clear nested screens using recursion
        ((screen as? ContainerScreen<*>)?.renderer as? ComposeRenderer<*>)?.clearScreens(stateHolder, clearAll = true)
    }

    private fun calculateRemovedScreens(oldState: NavigationState, newState: NavigationState): List<Screen> {
        val newChainSet = newState.getChildScreens()
        return oldState.getChildScreens().filter { it !in newChainSet }
    }

}