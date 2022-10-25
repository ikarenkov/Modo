package com.github.terrakok.modo.android.compose

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.MultiNavigation
import com.github.terrakok.modo.MultiReducer
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.StackNavigationState
import com.github.terrakok.modo.StackReducer
import kotlinx.parcelize.Parcelize

interface ComposeContent {
    @Composable
    fun Content()

    val screenKey: String
}

interface ComposeScreen : Screen, ComposeContent

val LocalContainerScreen = staticCompositionLocalOf<ComposeContainerScreen<*>> { error("no screen provided") }

abstract class ComposeContainerScreen<State : NavigationState>(
    initState: State,
    reducer: NavigationReducer<State>
) : ContainerScreen<State>(initState, reducer), ComposeScreen {
    override var navigationState: State
        get() = ((renderer as ComposeRenderer).state ?: (super.navigationState)) as State
        set(value) {
            super.navigationState = value
        }

    init {
        renderer = ComposeRenderer(containerScreen = this, exitAction = { })
    }

    @Composable
    protected fun InternalContent(
        screen: Screen,
        content: RendererContent = defaultRendererContent
    ) {
        val composeRenderer = renderer as ComposeRenderer
        composeRenderer.Content(screen as ComposeScreen, content)
    }

    override fun toString(): String = screenKey

}

open class Stack(
    rootScreen: ComposeScreen,
    reducer: NavigationReducer<StackNavigationState> = StackReducer(),
    override val screenKey: String = generateScreenKey(),
    val defaultRendererContent: RendererContent = com.github.terrakok.modo.android.compose.defaultRendererContent
) : ComposeContainerScreen<StackNavigationState>(
    StackNavigationState(listOf(rootScreen)),
    reducer,
) {

    /**
     * Default implementation last screen from stack.
     */
    @Composable
    override fun Content() {
        TopScreenContent()
    }

    /**
     * Renders last screen from stack.
     */
    @Composable
    protected fun TopScreenContent(
        content: RendererContent = defaultRendererContent
    ) {
        Content(navigationState.stack.last(), content)
    }

    @Composable
    protected fun Content(
        screen: Screen,
        content: RendererContent = defaultRendererContent
    ) {
        super.InternalContent(screen, content)
    }

}

open class MultiScreen(
    initState: MultiNavigation,
    reducer: NavigationReducer<MultiNavigation> = MultiReducer(),
    override val screenKey: String = generateScreenKey(),
) : ComposeContainerScreen<MultiNavigation>(
    initState,
    reducer,
) {

    @Composable
    override fun Content() {
        SelectedScreen()
    }

    @Composable
    fun SelectedScreen(
        content: RendererContent = defaultRendererContent
    ) {
        Content(navigationState.containers[navigationState.selected], content)
    }

    @Composable
    fun Content(
        screen: Screen,
        content: RendererContent = defaultRendererContent
    ) {
        // Завезти баг на IssueTracker гугла
        super.InternalContent(screen, content)
    }

}