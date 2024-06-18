package com.github.terrakok.modo

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.parcelize.Parcelize

@Parcelize
class MockScreen(
    override val screenKey: ScreenKey = generateScreenKey()
) : Screen {

    @Composable
    override fun Content(modifier: Modifier) = Unit
}