package com.github.terrakok.modo.android.compose

import android.os.Parcelable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.NavigationState
import com.github.terrakok.modo.Screen

interface ComposeContent {
    @Composable
    fun Content()

    val screenKey: String
}

interface ComposeScreen : Screen, ComposeContent, Parcelable

val LocalContainerScreen = staticCompositionLocalOf<ComposeContainerScreen<*>> { error("no screen provided") }

abstract class ComposeContainerScreen<State : NavigationState>(
    initState: State,
) : ContainerScreen<State>(initState), ComposeScreen {

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