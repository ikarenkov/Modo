package com.github.terrakok.modo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.github.terrakok.modo.animation.ScreenTransitionType
import com.github.terrakok.modo.animation.defaultCalculateTransitionType
import com.github.terrakok.modo.containers.ContainerScreen
import com.github.terrakok.modo.containers.LocalContainerScreen
import com.github.terrakok.modo.model.ScreenModelStore

typealias RendererContent = @Composable ComposeRendererScope.() -> Unit

val defaultRendererContent: (@Composable ComposeRendererScope.() -> Unit) = { screen.SaveableContent() }

internal val LocalSaveableStateHolder = staticCompositionLocalOf<SaveableStateHolder?> { null }

@Composable
fun Screen.SaveableContent() {
    LocalSaveableStateHolder.currentOrThrow.SaveableStateProvider(key = screenKey) {
        Content()
    }
}

class ComposeRendererScope(
    val screen: Screen,
    val transitionType: ScreenTransitionType
)

/**
 * Renderer responsibilities:
 *  1. Rendering - wrapping state to composable state and delegating rendering to screens
 *  2. Storing and clearing composable states inside [SaveableStateHolder]
 */
internal class ComposeRenderer(
    private val containerScreen: ContainerScreen<*>,
    private val getTransitionType: (oldState: NavigationState?, newState: NavigationState?) -> ScreenTransitionType =
        ::defaultCalculateTransitionType
) : NavigationRenderer {

    var state: NavigationState? by mutableStateOf(null, neverEqualPolicy())
        private set

    private val lastStackEvent: MutableState<ScreenTransitionType> = mutableStateOf(ScreenTransitionType.Idle)

    // TODO: share removed screen for whole structure
    private val removedScreens = mutableSetOf<Screen>()

    override fun render(state: NavigationState) {
        lastStackEvent.value = getTransitionType(this.state, state)
        this.state?.let { currentState ->
            removedScreens.addAll(calculateRemovedScreens(currentState, state))
        }
        this.state = state
    }

    @Composable
    fun Content(
        screen: Screen,
        content: RendererContent = defaultRendererContent
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
            LocalContainerScreen.current
            ComposeRendererScope(screen, lastStackEvent.value).content()
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
        ((screen as? ContainerScreen<*>)?.renderer as? ComposeRenderer)?.clearScreens(stateHolder, clearAll = true)
    }

    private fun calculateRemovedScreens(oldState: NavigationState, newState: NavigationState): List<Screen> {
        val newChainSet = newState.getChildScreens()
        return oldState.getChildScreens().filter { it !in newChainSet }
    }

}