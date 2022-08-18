package com.github.terrakok.modo.android.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.*

interface ComposeContent {
    @Composable
    fun Content()
}

abstract class ComposeScreen(
    id: String
) : Screen(id), ComposeContent {
    override fun toString() = id
}

open class ComposeContainerScreen(
    id: String,
    initState: StackNavigation,
    reducer: NavigationReducer = StackReducer()
) : ContainerScreen(id, initState, reducer), ComposeContent {
    constructor(id: String, rootScreen: ComposeScreen) : this(
        id,
        StackNavigation(listOf(rootScreen))
    )

    private val composeRenderer = ComposeNavigationRenderer {
        container.back()
    }

    init {
        renderer = composeRenderer
    }

    @Composable
    final override fun Content() = Content {
        composeRenderer.Content()
    }

    @Composable
    open fun Content(screenContent: @Composable () -> Unit) {
        Box(modifier = Modifier.fillMaxSize()) {
            screenContent()
        }
    }
}

abstract class ComposeMultiScreen(
    id: String,
    initState: MultiNavigation,
    reducer: NavigationReducer = MultiReducer()
) : ContainerScreen(id, initState, reducer), ComposeContent {
    constructor(id: String, selectedStack: Int, vararg rootScreens: ComposeScreen) : this(
        id,
        MultiNavigation(
            rootScreens.mapIndexed { i, s -> ComposeContainerScreen("$i", s) },
            selectedStack
        )
    )


    private val composeRenderer = ComposeNavigationRenderer {
        container.back()
    }

    init {
        renderer = composeRenderer
    }

    @Composable
    final override fun Content() {
        val selectedIndex = (composeRenderer.mutableState.value as? MultiNavigation)?.activeContainerIndex ?: return
        Content(selectedIndex) {
            composeRenderer.Content()
        }
    }

    @Composable
    abstract fun Content(stackIndex: Int, screenContent: @Composable () -> Unit)
}