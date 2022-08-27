package com.github.terrakok.modo.android.compose

import androidx.compose.runtime.Composable
import com.github.terrakok.modo.*

interface ComposeContent {
    @Composable
    fun Content()
}

interface ComposeScreen : Screen, ComposeContent

abstract class ComposeContainerScreen<State : NavigationState>(
    id: String,
    initState: State,
    reducer: NavigationReducer<State>
) : ContainerScreen<State>(id, initState, reducer), ComposeScreen {
    private val composeRenderer = ComposeRenderer(exitAction = { container?.dispatch(Back) })

    init {
        // TODO: maybe we can replace NavigationRenderer to ComposeRenderer? We are not going to support fragments anymore.
        renderer = composeRenderer
    }

    @Composable
    final override fun Content() {
        Content(navigationState) { composeRenderer.Content() }
    }

    @Composable
    open fun Content(state: State, screenContent: @Composable () -> Unit) {
        screenContent()
    }
}

open class Stack(id: String, rootScreen: ComposeScreen) :
    ComposeContainerScreen<StackNavigation>(id, StackNavigation(listOf(rootScreen)), StackReducer())

open class MultiStack(id: String, vararg rootScreen: ComposeScreen, selected: Int) :
    ComposeContainerScreen<MultiNavigation>(
        id,
        MultiNavigation(rootScreen.mapIndexed { i, s -> Stack(i.toString(), s) }, selected),
        MultiReducer()
    )