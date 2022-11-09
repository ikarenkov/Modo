package com.github.terrakok.modo.multiscreen

import androidx.compose.runtime.Composable
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.NavModel
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.RendererContent
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.defaultRendererContent

abstract class MultiScreen(
    navigationModel: MultiScreenNavModel
) : ContainerScreen<MultiScreenState>(navigationModel) {

    override val reducer: NavigationReducer<MultiScreenState> = MultiReducer()

    @Composable
    override fun Content() {
        SelectedScreen()
    }

    @Composable
    fun SelectedScreen(
        content: RendererContent<MultiScreenState> = defaultRendererContent
    ) {
        Content(navigationState.containers[navigationState.selected], content)
    }

    @Composable
    fun Content(
        screen: Screen,
        content: RendererContent<MultiScreenState> = defaultRendererContent
    ) {
        // report issue to google issue tracker
        super.InternalContent(screen, content)
    }

}