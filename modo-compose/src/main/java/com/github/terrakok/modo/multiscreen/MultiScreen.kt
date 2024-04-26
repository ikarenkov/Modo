package com.github.terrakok.modo.multiscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.RendererContent
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.defaultRendererContent

val LocalMultiScreenNavigation: ProvidableCompositionLocal<MultiScreenNavContainer> = staticCompositionLocalOf {
    error("There is no MultiScreenContainer in hierarchy, or maybe you override provideCompositionLocal and forgot to call supper.")
}

abstract class MultiScreen(
    navigationModel: MultiScreenNavModel
) : ContainerScreen<MultiScreenState, MultiScreenAction>(navigationModel), MultiScreenNavContainer {

    @Composable
    override fun Content(modifier: Modifier) {
        SelectedScreen()
    }

    override fun provideNavigationContainer() = LocalMultiScreenNavigation provides this

    @Composable
    fun SelectedScreen(
        modifier: Modifier = Modifier,
        content: RendererContent<MultiScreenState> = defaultRendererContent
    ) {
        val (screens, selectedPos) = navigationState
        Content(screens[selectedPos], modifier, content)
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