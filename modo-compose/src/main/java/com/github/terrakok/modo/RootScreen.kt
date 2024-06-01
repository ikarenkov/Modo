package com.github.terrakok.modo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.Modo.rememberRootScreen
import kotlinx.parcelize.Parcelize

@Parcelize
data class RootScreenState<T : Screen>(
    internal val screen: T
) : NavigationState {
    override fun getChildScreens(): List<Screen> = listOf(screen)
}

/**
 * Screen for single source of providing [LocalSaveableStateHolder]. Should be used with [Modo.rememberRootScreen] or [Modo.getOrCreateRootScreen].
 */
@Parcelize
class RootScreen<T : Screen> internal constructor(
    private val navModel: NavModel<RootScreenState<T>, NavigationAction<RootScreenState<T>>>
) : ContainerScreen<RootScreenState<T>, NavigationAction<RootScreenState<T>>>(
    navModel
) {

    constructor(screen: T) : this(NavModel(RootScreenState(screen)))

    val screen: T get() = navigationState.screen

    @Composable
    override fun Content(modifier: Modifier) {
        val stateHolder: SaveableStateHolder = LocalSaveableStateHolder.current ?: rememberSaveableStateHolder()
        CompositionLocalProvider(
            LocalSaveableStateHolder providesDefault stateHolder
        ) {
            InternalContent(navigationState.screen, modifier)
        }
    }

}