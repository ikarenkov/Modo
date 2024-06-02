package com.github.terrakok.modo.stack

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.terrakok.modo.animation.ScreenTransition
import kotlinx.parcelize.Parcelize

@Parcelize
class DefaultStackScreen(
    private val navModel: StackNavModel
) : StackScreen(navModel) {

    @Composable
    override fun Content(modifier: Modifier) {
        TopScreenContent(modifier) { modifier ->
            ScreenTransition(modifier)
        }
    }

}