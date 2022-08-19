package com.github.terrakok.modo.android.compose

import androidx.compose.runtime.Composable
import com.github.terrakok.modo.*

interface ComposeContent {
    @Composable
    fun Content()
}

interface ComposeScreen : Screen, ComposeContent

abstract class ComposeContainerScreen(
    id: String,
    initState: NavigationState,
    reducer: NavigationReducer
) : ContainerScreen(id, initState, reducer), ComposeContent {
    private val composeRenderer = ComposeNavigationRenderer { container?.dispatch(Back) }

    init {
        renderer = composeRenderer
    }

    @Composable
    final override fun Content() {
        val currentState = composeRenderer.currentState ?: return
        Content(currentState) { composeRenderer.Content() }
    }

    @Composable
    open fun Content(state: NavigationState, screenContent: @Composable () -> Unit) {
        screenContent()
    }
}

open class Stack(id: String, rootScreen: ComposeScreen) :
    ComposeContainerScreen(id, StackNavigation(listOf(rootScreen)), StackReducer())

open class MultiStack(id: String, vararg rootScreen: ComposeScreen, selected: Int) :
    ComposeContainerScreen(
        id,
        MultiNavigation(rootScreen.mapIndexed { i, s -> Stack(i.toString(), s) }, selected),
        MultiReducer()
    )