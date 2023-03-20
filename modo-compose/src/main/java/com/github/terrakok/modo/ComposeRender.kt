package com.github.terrakok.modo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.github.terrakok.modo.model.ScreenModelStore
import com.github.terrakok.modo.util.currentOrThrow

typealias RendererContent<State> = @Composable ComposeRendererScope<State>.() -> Unit

val defaultRendererContent: (@Composable ComposeRendererScope<*>.() -> Unit) = { screen.SaveableContent() }

internal val LocalSaveableStateHolder = staticCompositionLocalOf<SaveableStateHolder?> { null }

@Composable
fun Screen.SaveableContent() {
    LocalSaveableStateHolder.currentOrThrow.SaveableStateProvider(key = screenKey) {
        Content()
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
        // use single state holder for whole hierarchy, store it on top of it
        val stateHolder: SaveableStateHolder = LocalSaveableStateHolder.current ?: rememberSaveableStateHolder()
        DisposableEffect(key1 = state) {
            onDispose {
                clearScreens(stateHolder)
            }
        }
        CompositionLocalProvider(
            LocalSaveableStateHolder providesDefault stateHolder,
            LocalContainerScreen provides containerScreen
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