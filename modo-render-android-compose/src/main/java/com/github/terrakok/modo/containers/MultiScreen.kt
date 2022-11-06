package com.github.terrakok.modo.containers

import androidx.compose.runtime.Composable
import com.github.terrakok.modo.MultiNavigation
import com.github.terrakok.modo.MultiReducer
import com.github.terrakok.modo.NavigationReducer
import com.github.terrakok.modo.RendererContent
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.defaultRendererContent

abstract class MultiScreen(
    navigationModel: NavigationModel<MultiNavigation>
) : ContainerScreen<MultiNavigation>(navigationModel) {

    override val reducer: NavigationReducer<MultiNavigation> = MultiReducer()

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
        // report issue to google issue tracker
        super.InternalContent(screen, content)
    }

}