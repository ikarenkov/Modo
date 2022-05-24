package com.github.terrakok.modo.android.compose

import android.app.Activity
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import com.github.terrakok.modo.NavigationDispatcher
import com.github.terrakok.modo.NavigationRender
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.Screen

typealias RendererContent = @Composable ComposeRendererScope.() -> Unit

val defaultRendererContent: RendererContent = { screen.SaveableContent() }

internal val LocalSaveableStateHolder = staticCompositionLocalOf<SaveableStateHolder?> { null }

@Composable
fun ComposeScreen.SaveableContent() {
    LocalSaveableStateHolder.currentOrThrow.SaveableStateProvider(key = screenKey) {
        Content()
    }
}

class ComposeRendererScope(
    val screen: ComposeScreen,
    val transitionType: ScreenTransitionType
)

interface ComposeRenderer : NavigationRender {

    val state: State<NavigationState>

    @Composable
    fun Content()

    fun clearStateHolder(stateHolder: SaveableStateHolder, clearAll: Boolean = false)

}

class ComposeRenderImpl(
    private val exitAction: () -> Unit,
    private val getTransitionType: (oldScreensStack: List<Screen>, newScreensStack: List<Screen>) -> ScreenTransitionType = ::defaultCalculateTransitionType,
    private val content: RendererContent = defaultRendererContent
) : ComposeRenderer {

    override val state: MutableState<NavigationState> = mutableStateOf(NavigationState())

    private val lastStackEvent: MutableState<ScreenTransitionType> = mutableStateOf(ScreenTransitionType.Idle)

    // TODO: share removed screen for whole structure
    private val removedScreens = mutableSetOf<Screen>()

    override fun invoke(state: NavigationState) {
        if (state.chain.isEmpty()) {
            exitAction()
        }
        lastStackEvent.value = getTransitionType(this.state.value.chain, state.chain)
        removedScreens.addAll(calculateRemovedScreens(this.state.value.chain, state.chain))
        this.state.value = state
    }

    @Composable
    override fun Content() {
        val stateHolder: SaveableStateHolder = LocalSaveableStateHolder.current ?: rememberSaveableStateHolder()
        DisposableEffect(key1 = state.value) {
            onDispose {
                clearStateHolder(stateHolder)
            }
        }
        CompositionLocalProvider(
            LocalSaveableStateHolder providesDefault stateHolder
        ) {
            state.value.chain.lastOrNull()?.let { screen ->
                require(screen is ComposeScreen) {
                    "ComposeRender works with ComposeScreen only! Received $screen"
                }
                ComposeRendererScope(screen, lastStackEvent.value).content()
            }
        }
    }

    /**
     * Clear states of removed screens from given [stateHolder].
     * @param stateHolder - SaveableStateHolder that contains screen states
     * @param clearAll - forces to remove all screen states that renderer holds (removed and "displayed")
     */
    override fun clearStateHolder(stateHolder: SaveableStateHolder, clearAll: Boolean) {
        if (clearAll) {
            state.value.chain.clearStates(stateHolder)
        }
        removedScreens.clearStates(stateHolder)
        if (removedScreens.isNotEmpty()) {
            removedScreens.clear()
        }
    }

    private fun Iterable<Screen>.clearStates(stateHolder: SaveableStateHolder) = forEach { screen ->
        require(screen is ComposeScreen)
        stateHolder.removeState(screen.screenKey)
        (screen as? NestedNavigationComposeScreen)?.onRemoveScreen(stateHolder)
    }

    private fun calculateRemovedScreens(currentChain: List<Screen>, newChain: List<Screen>): List<Screen> {
        val newChainSet = newChain.toSet()
        return currentChain.filter { it !in newChainSet }
    }

}

/**
 * Renderer that must be used as root renderer for [Modo] to provide root [LocalNavigationDispatcher] for composable tree.
 */
class RootRenderer(
    private val rootNavigationDispatcher: NavigationDispatcher,
    private val original: ComposeRenderer
) : ComposeRenderer by original {

    constructor(
        activity: Activity,
        rootNavigationDispatcher: NavigationDispatcher,
    ) : this(
        rootNavigationDispatcher,
        ComposeRenderImpl({ activity.finish() })
    )

    @Composable
    override fun Content() {
        CompositionLocalProvider(LocalNavigationDispatcher provides rootNavigationDispatcher) {
            original.Content()
        }
    }

}
