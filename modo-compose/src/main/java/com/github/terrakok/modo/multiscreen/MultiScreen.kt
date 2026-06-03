package com.github.terrakok.modo.multiscreen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ProvidedValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.ContainerScreen
import com.github.terrakok.modo.RendererContent
import com.github.terrakok.modo.Screen
import com.github.terrakok.modo.defaultRendererContent

val LocalMultiScreenNavigation: ProvidableCompositionLocal<MultiScreenNavContainer> = staticCompositionLocalOf {
    error("There is no LocalMultiScreenNavigation in hierarchy, or maybe you override provideCompositionLocal and forgot to call super.")
}

/**
 * Provides the nearest [MultiScreen] in the composition.
 */
val LocalMultiScreen: ProvidableCompositionLocal<MultiScreen> = staticCompositionLocalOf {
    error("There is no LocalMultiScreen in hierarchy. Wrap in MultiScreen or provide it manually.")
}

abstract class MultiScreen(
    navigationModel: MultiScreenNavModel
) : ContainerScreen<MultiScreenState>(navigationModel), MultiScreenNavContainer {

    @Composable
    override fun Content(modifier: Modifier) {
        SelectedScreen()
    }

    override fun provideNavigationContainer(): ProvidedValue<out MultiScreenNavContainer> =
        LocalMultiScreenNavigation provides this

    override fun provideCompositionLocals(): Array<ProvidedValue<*>> = arrayOf(
        LocalMultiScreenNavigation provides this,
        LocalMultiScreen provides this,
    )

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
        modifier: Modifier = Modifier,
        content: RendererContent<MultiScreenState> = defaultRendererContent
    ) {
        // report issue to google issue tracker
        super.InternalContent(screen, modifier, content)
    }

}