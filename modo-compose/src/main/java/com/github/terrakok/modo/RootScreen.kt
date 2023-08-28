package com.github.terrakok.modo

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.saveable.SaveableStateHolder
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import kotlinx.parcelize.Parcelize

@Parcelize
class RootScreen<T : Screen>(
    val screen: T,
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content() {
        val stateHolder: SaveableStateHolder = LocalSaveableStateHolder.current ?: rememberSaveableStateHolder()
        CompositionLocalProvider(
            LocalSaveableStateHolder providesDefault stateHolder
        ) {
            screen.Content()
        }
    }

}