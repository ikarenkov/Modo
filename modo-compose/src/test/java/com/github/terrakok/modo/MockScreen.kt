package com.github.terrakok.modo

import androidx.compose.runtime.Composable
import kotlinx.parcelize.Parcelize

@Parcelize
class MockScreen(
    override val screenKey: ScreenKey
) : Screen {

    @Composable
    override fun Content() = Unit
}