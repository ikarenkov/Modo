package com.github.terrakok.modo.multiscreen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.RendererContent
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.defaultRendererContent

abstract class MultiScreen(
    navigationModel: MultiScreenNavModel
) : ContainerScreen<MultiScreenState, MultiScreenAction>(navigationModel), MultiScreenContainer {

    @Composable
    override fun Content(modifier: Modifier) {
        SelectedScreen()
    }

    @Composable
    fun SelectedScreen(
        modifier: Modifier = Modifier,
        content: RendererContent<MultiScreenState> = defaultRendererContent
    ) {
        Content(navigationState.containers[navigationState.selected], modifier, content)
    }

    @Composable
    fun Content(
        screen: Screen,
        modifier: Modifier,
        content: RendererContent<MultiScreenState> = defaultRendererContent
    ) {
        // report issue to google issue tracker
        super.InternalContent(screen, modifier, content)
    }

}