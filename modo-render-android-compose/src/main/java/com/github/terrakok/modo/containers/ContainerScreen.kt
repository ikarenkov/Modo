package com.github.terrakok.modo.containers

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.github.terrakok.modo.ComposeRenderer
import com.github.terrakok.modo.NavigationAction
import com.github.terrakok.modo.NavigationDispatcher
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.NavigationRenderer
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.RendererContent
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.defaultRendererContent

val LocalContainerScreen = staticCompositionLocalOf<ContainerScreen<*>> { error("no screen provided") }

abstract class ContainerScreen<State : NavigationState>(
    initState: State,
) : Screen, NavigationDispatcher {

    abstract val reducer: NavigationReducer<State>

    var renderer: NavigationRenderer? = null
        set(value) {
            field = value
            value?.render(navigationState)
        }
    var navigationState: State = initState
        get() = ((renderer as ComposeRenderer).state ?: field) as State
        set(value) {
            field = value
            renderer?.render(value)
        }

    init {
        renderer = ComposeRenderer(containerScreen = this)
    }

    override fun dispatch(action: NavigationAction) {
        navigationState = reducer.reduce(action, navigationState)
    }

    @Composable
    protected fun InternalContent(
        screen: Screen,
        content: RendererContent = defaultRendererContent
    ) {
        val composeRenderer = renderer as ComposeRenderer
        composeRenderer.Content(screen as Screen, content)
    }

    override fun toString(): String = screenKey

}