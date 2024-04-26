package com.github.terrakok.modo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import kotlinx.parcelize.Parcelize

@Parcelize
class RootScreenState<T : Screen>(
    private val screen: T
) : NavigationState {
    override fun getChildScreens(): List<Screen> = listOf(screen)
}

/**
 * Screen for single source of providing [LocalSaveableStateHolder]. Should be used with [Modo.init].
 */
@Parcelize
class RootScreen<T : Screen>(
    val screen: T,
    private val navModel: NavModel<RootScreenState<T>, NavigationAction<RootScreenState<T>>> =
        NavModel(RootScreenState(screen))
) : ContainerScreen<RootScreenState<T>, NavigationAction<RootScreenState<T>>>(
    navModel
) {

    @Composable
    override fun Content(modifier: Modifier) {
        val stateHolder: SaveableStateHolder = LocalSaveableStateHolder.current ?: rememberSaveableStateHolder()
        CompositionLocalProvider(
            LocalSaveableStateHolder providesDefault stateHolder
        ) {
            InternalContent(screen, modifier)
        }
    }

}