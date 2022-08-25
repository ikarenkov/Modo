package com.github.terrakok.modo.android.compose

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.staticCompositionLocalOf
import com.github.terrakok.modo.MultiNavigation
import com.github.terrakok.modo.NavigationRenderer
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.StackNavigation

typealias RendererContent = @Composable ComposeRendererScope.() -> Unit

val defaultRendererContent: RendererContent = { screen.SaveableContent() }

internal val LocalSaveableStateHolder = staticCompositionLocalOf<SaveableStateHolder?> { null }

@Composable
fun ComposeScreen.SaveableContent() {
    LocalSaveableStateHolder.currentOrThrow.SaveableStateProvider(key = id) {
        Content()
    }
}

class ComposeRendererScope(
    val screen: ComposeScreen,
    val transitionType: ScreenTransitionType
)

fun Activity.ComposeRenderer(
    getTransitionType: (oldState: NavigationState?, newState: NavigationState?) -> ScreenTransitionType =
        ::defaultCalculateTransitionType,
    content: RendererContent = defaultRendererContent
) = ComposeRenderer(
    exitAction = { finish() },
    getTransitionType,
    content
)

/**
 * Renderer responsibilities:
 *  1. Rendering - wrapping state to composable state and delegating rendering to screens
 *  2. Storing and clearing composable states inside [SaveableStateHolder]
 *  3. Animations
 */
class ComposeRenderer(
    private val exitAction: () -> Unit = {},
    private val getTransitionType: (oldState: NavigationState?, newState: NavigationState?) -> ScreenTransitionType =
        ::defaultCalculateTransitionType,
    private val content: RendererContent = defaultRendererContent
) : NavigationRenderer {

    private val mutableState: MutableState<NavigationState?> = mutableStateOf(null)
    private var state
        get() = mutableState.value
        set(value) {
            mutableState.value = value
        }

    private val lastStackEvent: MutableState<ScreenTransitionType> = mutableStateOf(ScreenTransitionType.Idle)

    // TODO: share removed screen for whole structure
    private val removedScreens = mutableSetOf<Screen>()

    override fun render(state: NavigationState) {
        if (state is StackNavigation && state.stack.isEmpty()) {
            exitAction()
        } else {
            lastStackEvent.value = getTransitionType(this.state, state)
            this.state?.let { currentState ->
                removedScreens.addAll(calculateRemovedScreens(currentState, state))
            }
            this.state = state
        }
    }

    @Composable
    fun Content() {
        // use single state holder for whole hierarchy, store it on top of it
        val stateHolder: SaveableStateHolder = LocalSaveableStateHolder.current ?: rememberSaveableStateHolder()
        DisposableEffect(key1 = state) {
            onDispose {
                clearStateHolder(stateHolder)
            }
        }
        CompositionLocalProvider(
            LocalSaveableStateHolder providesDefault stateHolder
        ) {
            state?.getScreen()?.let { screen ->
                require(screen is ComposeScreen) {
                    "ComposeRender works with ComposeScreen only! Received $screen"
                }
                ComposeRendererScope(screen, lastStackEvent.value).content()
            }
        }
    }

    // TODO: move it to screen to bring flexibility to our library and remove else branch with exception
    private fun NavigationState.getScreen(): ComposeContent = when (this) {
        is StackNavigation -> stack.last() as ComposeContent
        is MultiNavigation -> containers[selected] as ComposeContent
        else -> throw IllegalStateException("Unknown navigation state: $this")
    }

    /**
     * Clear states of removed screens from given [stateHolder].
     * @param stateHolder - SaveableStateHolder that contains screen states
     * @param clearAll - forces to remove all screen states that renderer holds (removed and "displayed")
     */
    private fun clearStateHolder(stateHolder: SaveableStateHolder, clearAll: Boolean = false) {
        if (clearAll) {
            state?.getChildScreens()?.clearStates(stateHolder)
        }
        removedScreens.clearStates(stateHolder)
        if (removedScreens.isNotEmpty()) {
            removedScreens.clear()
        }
    }

    private fun Iterable<Screen>.clearStates(stateHolder: SaveableStateHolder) = forEach { screen ->
        require(screen is ComposeScreen)
        stateHolder.removeState(screen.id)
        // clear nested screens using recursion
        ((screen as? ComposeContainerScreen<*>)?.renderer as? ComposeRenderer)?.clearStateHolder(stateHolder, clearAll = true)
    }

    private fun calculateRemovedScreens(oldState: NavigationState, newState: NavigationState): List<Screen> {
        val newChainSet = newState.getChildScreens()
        return oldState.getChildScreens().filter { it !in newChainSet }
    }

}